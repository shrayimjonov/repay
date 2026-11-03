# Autodebit Redesign — Solution Design

**Author:** Solution Architect (claude-opus-5)
**Project:** repay / autodebit
**Date:** 2026-09-02
**Status:** DRAFT — awaiting human approval before implementation
**Optimizes for:** throughput/latency + reliability/idempotency at 80K-debtor scale

---

## 1. Problem statement

The autodebit pipeline debits ~80,000 borrowers per cycle (card-not-present
acquiring: debit borrower card, credit lender EPOS) across UZCARD, Humo and
SV-GATE. We need it **highly available, high-throughput, fast, reliable, and
idempotent** end to end.

The audit below shows the prod pipeline is functionally a **v5 cycle
orchestrator bolted onto the V2 debit engine**, with a **second independent V2
scheduler still running for insolvent loans**. It works, but it has concrete
reliability and idempotency holes and a throughput ceiling that barely fits a
cycle into its own deadline window.

This redesign **consolidates onto one path (evolve v5, retire the rest)** and
closes the safety and performance gaps — it does not introduce a "V6" parallel
generation.

---

## 2. Audit — what is actually running on `prod`

Traced from live `@Scheduled` / `@KafkaListener` beans on the `prod` branch:

| Generation | Status on prod | Evidence |
|---|---|---|
| V1 `AutoPaymentServiceImpl` | **disabled** | `@Scheduled` / `@PostConstruct` commented out |
| V2 `AutoPaymentServiceV2Impl` | **partially live** | `startInsolventAutoPayment` `@Scheduled` **active**; also the debit engine v5 calls into |
| V3 / V4 | **disabled** | V4 `@Scheduled` commented out |
| **v5** `DebitCyclePublisher` + `DebitCycleConsumer` | **LIVE (primary)** | active `@Scheduled` publisher + `@KafkaListener` consumer |

**Effective runtime topology (today):**

```
[v5 DebitCyclePublisher @Scheduled 10s, ShedLock]
    -> load ALL debtors from loan-service (paged 5k) into memory
    -> publish to Kafka topic "autodebit-topic-v2" (key=loanExtId, round-robin partition)
[v5 DebitCycleConsumer @KafkaListener concurrency=10, batch=true]
    -> virtual-thread per message, Semaphore(MAX_IN_FLIGHT_DEBITS=10)
    -> DB claim(loanExtId, cycleId)  [INSERT ... ON CONFLICT DO NOTHING]
    -> DebitThroughputLimiter (token bucket, static INSTANCE_COUNT=10)
    -> V2 processLoanDirect -> DebitStarterService -> payment-service payV2/payV3

[V2 startInsolventAutoPayment @Scheduled, ShedLock]   <-- SECOND, PARALLEL PATH
    -> PaymentWorkerV2 -> DebitStarterService -> payment-service   (NO DB claim)
```

**Capacity math (today):** `TARGET_DEBTORS_PER_MINUTE=4000`, so 80K debtors
≈ **20 min/cycle** against a `PROCESSING_INTERVAL_MILLIS=30 min` deadline. The
cycle only just fits, leaving little headroom for gateway slowdowns.

### 2.1 Findings (ranked)

**F1 — Kafka offsets commit before the debit finishes (reliability, HIGH).**
`DebitCycleConsumer.awaitBatch` joins with a 240s timeout and then *returns
normally* — the comment states "stragglers continue async, offsets commit
anyway." With `batch=true` the container commits offsets after the method
returns. If the pod dies after commit but before a straggler debit completes,
**the message is never redelivered**. The only backstop is cycle-level
non-drain + janitor moving stale `PROCESSING` → `EXPIRED_UNKNOWN`, which
**skips** the debtor rather than retrying → a borrower can silently miss a cycle.

**F2 — No end-to-end idempotency key to the gateway (idempotency, HIGH).**
`PayRequestDTO` carries no idempotency key. The DB `claim` only dedupes
*processing within a single cycleId*. On any cross-cycle retry the actual debit
relies entirely on the payment service's own (unverified) dedup. Convention
says `IdempotencyKey = loanExtId + cycleId + attempt` but it is **not
propagated** to `payV2/payV3`.

**F3 — Two overlapping debit paths without a shared lock (idempotency, HIGH).**
The v5 solvent path claims `(loanExtId, cycleId)`. The **insolvent V2 scheduler
path does not claim at all**, and the manual path claims under a *random*
`manual-<uuid>` cycleId. Because the uniqueness key includes `cycleId`, the same
`loanExtId` can be claimed under different cycle ids simultaneously → **concurrent
double debit of one loan is not structurally prevented** across paths.

**F4 — Unknown-outcome has cooldown but no reconciliation (two-leg, HIGH).**
On timeout/5xx the card loop correctly **stops** (avoids double-charge) and marks
`FAILED_UNKNOWN` with a 30-min cooldown. But there is **no status-query /
reconciliation** that later asks the gateway "did that debit actually land?".
After cooldown it simply retries next cycle — safe only if the gateway is
idempotent (see F2). This violates the rule *every async op must have a
reconciliation strategy*.

**F5 — Static `INSTANCE_COUNT=10` rate limiter (throughput, MEDIUM).**
Global throughput is `TARGET/INSTANCE_COUNT` per pod, hard-coded to 10. If Docker
Swarm runs ≠10 replicas (scale-out, failover, rolling deploy), the **aggregate
gateway rate is wrong** — either overshoots gateway limits or throttles below
target. No global coordination.

**F6 — Per-debtor Feign fan-out, no batching/caching (latency, MEDIUM).**
Each debtor triggers sequential calls: `getLoansCardsV2` → `getLoanAdditionalInfo`
→ `getCourse` → `payV2/V3`. `getCourse` (USD rate) is fetched **per loan** though
it is constant within a cycle. 3–4 serial round-trips × 80K amplifies latency and
downstream load.

**F7 — Whole-snapshot in memory on one node (availability, MEDIUM).**
The publisher loads all ~80K executable debtors into a `LinkedHashMap` before
publishing. Single-node memory pressure; the publisher is a ShedLock singleton
(fine for HA of *publishing*, but the in-memory snapshot is a fragility point).

**F8 — Non-atomic claim→debit→markStatus (observability/idempotency, MEDIUM).**
`claim`, the debit, and `markStatus` are separate transactions. A crash between a
*successful* debit and `markStatus` leaves the row `PROCESSING` → janitor →
`EXPIRED_UNKNOWN`. No double debit (cooldown), but the record is **wrong** and
there is no gateway-truth reconciliation to correct it (see F4).

---

## 3. Target architecture

**Principle: one pipeline, boring and proven.** Keep Kafka + PostgreSQL +
Resilience4j. Evolve v5; delete V1/V3/V4; fold the insolvent path into the same
claimed, idempotent pipeline. Every money mutation gets a durable idempotency
key and a reconciliation path.

### 3.1 Components

1. **Cycle Planner** (replaces `DebitCyclePublisher` scheduling role)
   - ShedLock singleton. Opens a `debit_cycle`, streams debtors from loan-service
     **page-by-page straight into Kafka** (no full in-memory snapshot — fixes F7).
   - Emits **both** solvent and insolvent debtors into the same topic with a
     `segment` field, retiring the separate V2 insolvent scheduler (fixes F3).
   - Writes one `debit_attempt` row per debtor in `PENDING` at publish time so
     `expected_count` is exact and drain detection is precise.

2. **Debit Consumer** (evolve `DebitCycleConsumer`)
   - `@KafkaListener`, **`AckMode.MANUAL_IMMEDIATE`**, ack **only after** the
     attempt reaches a terminal/parked state (fixes F1). Partition key stays
     `loanExtId` (one loan = one consumer at a time).
   - Bounded in-flight via semaphore + virtual threads (keep), but **ack per
     record after completion**, not per batch-with-timeout.

3. **Idempotent Debit Executor** (wraps V2 `DebitStarterService` money logic)
   - Computes `idempotencyKey = sha256(loanExtId | cycleId | attemptNo)` and
     **passes it to the payment service** on `payV2/payV3` (fixes F2). Requires a
     payment-service contract change (see §7 dependency).
   - Single claim keyed on **`(loanExtId, cycleId)`** with a **global
     anti-overlap guard**: a partial unique index ensuring at most one
     *non-terminal* attempt per `loanExtId` across all cycles (fixes F3).

4. **Distributed Throughput Governor** (replaces `DebitThroughputLimiter`)
   - Replace static `INSTANCE_COUNT` with a **live replica count** (Resilience4j
     rate limiter fed by an actual instance count from Consul/Eureka
     registrations, or a Redis token bucket for a true global rate). Target rate
     becomes elastic and correct under scaling (fixes F5).

5. **Reconciliation Worker** (new, closes F4/F8)
   - Periodically selects `FAILED_UNKNOWN` / `EXPIRED_UNKNOWN` attempts past a
     short grace window and calls a **payment-service status-query** by
     `idempotencyKey` to learn the gateway truth: `COMPLETED` (settle record,
     no retry), `NOT_FOUND` (safe to retry), or still `UNKNOWN` (hold + alert).
   - This is the reversal/settlement decision point for the two-leg problem.

6. **Enrichment Cache** (fixes F6)
   - Fetch the USD course **once per cycle** and cache it; batch card/loan lookups
     where the downstream APIs allow. Removes 1 serial round-trip per debtor and
     cuts load on payment/loan services.

### 3.2 Data flow (trigger → completion)

```
1. Planner opens cycle C (deadline = now + PROCESSING_INTERVAL).
2. Planner streams debtor pages; for each debtor:
     - INSERT debit_attempt(loan, C, PENDING, idempotency_key)  [ON CONFLICT DO NOTHING]
     - publish DebitMessage(loan, branch, segment, C, deadline, idempotencyKey)
       to autodebit topic (key=loanExtId).
   Planner sets expected_count = rows inserted.
3. Consumer receives record:
     - claim: PENDING -> PROCESSING (atomic UPDATE ... WHERE status='PENDING'
       AND no other non-terminal attempt for loan). If claim fails -> ack, skip.
     - Governor.acquire(deadline). If past deadline -> mark STALE_DROPPED, ack.
     - Executor.debit(loan, amount, idempotencyKey):
         debit card leg -> on definitive success: record COMPLETED
                        -> on definitive reject: record FAILED(reason)
                        -> on timeout/5xx: record FAILED_UNKNOWN (NO 2nd card)
     - ack record (MANUAL_IMMEDIATE) only now.
4. Reconciliation worker (async): for UNKNOWN attempts past grace ->
     payment.status(idempotencyKey) -> COMPLETED | retry | hold+alert.
5. Cycle drains when processed==expected OR deadline passes; Planner closes it.
```

### 3.3 Data model changes

`debit_attempt` (evolve existing):
- add `idempotency_key VARCHAR(64) NOT NULL` — sha256(loan|cycle|attempt).
- add `attempt_no INT NOT NULL DEFAULT 1`.
- add `segment VARCHAR(16)` — SOLVENT | INSOLVENT.
- add `gateway_ref VARCHAR(64)` — gateway transaction id when known.
- add `amount BIGINT`, `processing VARCHAR(24)` — for reconciliation & audit.
- status enum extended: `PENDING, PROCESSING, COMPLETED, FAILED, FAILED_UNKNOWN,`
  `EXPIRED_UNKNOWN, STALE_DROPPED, SKIPPED_*, RECONCILED_COMPLETED, HELD`.
- **Indexes:**
  - unique `(loan_ext_id, cycle_id)` (existing dedup).
  - **partial unique** `(loan_ext_id) WHERE status IN ('PENDING','PROCESSING')`
    — the global anti-overlap guard (F3).
  - `(status, created_at)` — janitor/reconciliation scans (avoid full-table).
  - `(cycle_id, status)` — drain counting.

`debit_cycle`: unchanged shape; `expected_count` now set at publish time and
authoritative.

### 3.4 Idempotency model (explicit)

- **Layer 1 — orchestration:** `debit_attempt` partial-unique guarantees at most
  one live attempt per loan globally.
- **Layer 2 — delivery:** Kafka redelivery is safe because claim is an atomic
  `PENDING→PROCESSING` compare-and-set; a redelivered record whose row is already
  terminal/processing is acked and dropped.
- **Layer 3 — money:** `idempotencyKey` sent to the gateway so a retried debit
  that already landed is a no-op at the payment service, not a double charge.
  *(Depends on payment-service support — see §7.)*

---

## 4. Failure modes & mitigations

| # | Failure | Mitigation |
|---|---|---|
| 1 | Consumer pod dies mid-debit | MANUAL_IMMEDIATE ack after completion → Kafka redelivers; claim CAS makes redelivery idempotent |
| 2 | Gateway timeout (money may have moved) | `FAILED_UNKNOWN`, stop card loop, **no** immediate retry; reconciliation queries gateway truth by idempotencyKey |
| 3 | Duplicate Kafka delivery | atomic `PENDING→PROCESSING` CAS; terminal rows acked & dropped |
| 4 | Two paths debit same loan | single topic + partial-unique `(loan) WHERE non-terminal`; insolvent path folded in |
| 5 | Network partition to payment | Resilience4j circuit breaker + bounded retry w/ jitter; open circuit → fast-fail → attempt stays PENDING, retried next cycle |
| 6 | Out-of-order same loan | partition key=loanExtId keeps one loan on one consumer; CAS serializes |
| 7 | Replica count changes | Governor uses live registry count (not static 10) → aggregate rate stays correct |
| 8 | Crash between debit success & status write | reconciliation reconciles PROCESSING/UNKNOWN against gateway truth → `RECONCILED_COMPLETED` |
| 9 | Poison message | parse failure → dead-letter topic (retain existing DLT behavior), never block partition |
| 10 | Loan-service page failure mid-stream | planner records partial expected_count for what published; unpublished debtors caught next cycle; alert on short pages |

---

## 5. Observability

- **Metrics (Micrometer):** `autodebit.cycle.duration`, `debtors.published`,
  `attempts.by_status` (counter per terminal status), `gateway.latency` &
  `gateway.errors` per gateway (UZCARD/Humo/SV-GATE), `governor.permits.wait_ms`,
  `inflight.debits`, `reconciliation.resolved{outcome}`, `kafka.consumer.lag`.
- **Alerts:** cycle exceeds deadline; `FAILED_UNKNOWN` rate > threshold;
  reconciliation `HELD` count > 0; consumer lag > cycle budget; circuit-breaker
  open per gateway; publish short-page anomaly.
- **Logs (structured JSON, existing convention):** `event`, `traceId`,
  `loanExtId`, `cycleId`, `gateway`, `idempotencyKey`, `durationMs`, `status`.
  **Card PAN masked to last-4 only.** No passport/full phone/address.

---

## 6. Trade-offs

- **Ack-after-completion (MANUAL_IMMEDIATE)** costs some throughput vs.
  commit-and-forget, but eliminates silent debtor loss (F1). Worth it for a money
  pipeline. Throughput is recovered via the enrichment cache (F6) and elastic
  governor (F5).
- **Partial-unique global guard** adds one index and can reject a legitimate
  re-attempt while a prior one is still non-terminal; the reconciliation worker +
  janitor bound how long a row stays non-terminal.
- **Gateway idempotency key** requires a payment-service contract change (cost:
  cross-team). Alternative (autodebit-only dedup) cannot prevent double charge on
  genuine gateway retries, so the key is required for true idempotency.
- **Redis/registry-based global rate** adds a dependency vs. the static constant;
  chosen because correctness under scaling matters more than simplicity here.
- Keeping Kafka + PostgreSQL + Resilience4j (boring/proven) over any new
  streaming/DB tech.

---

## 7. Cross-service dependencies (scan of prod contracts)

- **payment-service:** must accept an **idempotency key** on `payV2/payV3` and
  expose a **status-query by idempotency key** (for reconciliation). This is the
  critical external dependency and gates true end-to-end idempotency (F2/F4).
- **loan-service:** paged debtor feed (`getAllDebtLoansPageV2`) + insolvent feed;
  used for streaming publish. No change required, but confirm stable paging.
- **card-service:** `getLoansCardsV2`, `deactivateCard`, `getCardByPan`. Consider
  a batch card-lookup endpoint to reduce per-debtor round-trips (F6, optional).

---

## 8. Out of scope

- Rewriting the payment-service gateway integrations themselves (UZCARD/Humo/
  SV-GATE adapters) — this design consumes them via Feign.
- Central Bank clearing/settlement mechanics beyond recording gateway truth.
- Front-end / reporting changes.
- Migrating historical `debit_attempt` data beyond an additive schema change.

---

## 9. Migration plan (safe rollout)

1. Additive DB migration (new columns/indexes; nullable/backfilled).
2. Ship idempotency-key propagation **behind a flag**, dark-launched (compute +
   log key before payment-service supports it).
3. Land payment-service idempotency-key + status-query (dependency).
4. Switch consumer to MANUAL_IMMEDIATE ack + partial-unique guard.
5. Fold insolvent segment into the unified pipeline; **remove** V2 insolvent
   scheduler and delete V1/V3/V4 dead code.
6. Enable reconciliation worker + elastic governor.
7. Delete `DebitThroughputLimiter` static-instance logic last, after governor is
   proven.

---

## Signal: DESIGN_COMPLETE
Ready for human review, then implementation by the Coder (claude-opus-5).
Open decisions requiring human input are listed in the orchestrator hand-off.
