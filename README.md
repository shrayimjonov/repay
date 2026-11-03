# Repay — Loan Repayment & Collection Microservice Platform

**Repay** is a microservice platform at Davrbank that automates loan repayment. It links customer cards from all
national payment systems, debits overdue loans automatically, accepts payments, scores customers, supports
court/collection workflows and produces repayment reports.

> **Context.** Built by the Davrbank engineering team. I work there as a Backend Engineer (Oct 2025 – present) and
> contributed mainly to the card, auto-debit, payment, logging and API-gateway services. Published as a portfolio sample with all
> confidential data removed — see [Security & sanitization](#security--sanitization).

## Architecture

```
            ┌──────────┐
 clients ──▶│ apigate  │  Spring Cloud Gateway · Keycloak (OAuth2/JWT) · Resilience4j
            └────┬─────┘
     ┌───────────┼──────────────┬──────────────┬─────────────┐
     ▼           ▼              ▼              ▼             ▼
   card      autodebit       payment         loan         scoring ... (≈20 services)
     │           │              │              │             │
     └──── Kafka (events) ── PostgreSQL / MongoDB / Redis ── Consul/Eureka · Zipkin ───┘
                                    │
                        log-aggregation ──▶ ClickHouse
```

## Services

| Service | Purpose | Stack |
|---|---|---|
| `apigate` | API gateway, routing, rate limiting, circuit breakers | Spring Cloud Gateway, WebFlux, Resilience4j |
| `card` ⭐ | Card linking and tokenization for Humo, Uzcard, Visa, Mastercard; balance checks, card management | Java 21, PostgreSQL, MongoDB, Redis, Kafka, Quartz |
| `autodebit` ⭐ | Scheduled automatic debiting of overdue loans from linked cards | Java 21, WebFlux, Kafka, MongoDB |
| `payment` ⭐ | Payment processing, payment sessions, reconciliation, Excel exports | Java 21, PostgreSQL, Kafka, Apache POI |
| `log-aggregation` ⭐ | Centralized request/audit logging library plus REST API on ClickHouse | Java 21, ClickHouse, Kafka |
| `loan` | Loans, guarantors, debt data (REST + GraphQL) | Java 21, PostgreSQL, MongoDB |
| `scoring` | Customer scoring and card limits | Java 21 |
| `report` | Repayment reports (PDF/Excel) | Java 21, ClickHouse, iText, POI |
| `notification` | SMS/push notifications | Java 23, Kafka, MongoDB |
| `client`, `court`, `user`, `processing`, `payment-frame`, `lg-protocol`, `log-protocol` | Customers, court cases, users, processing-center integration, logging protocols | Kotlin, Spring Boot 3.4 |
| `file`, `chat`, `trusted-user`, `keycloak-*` | File storage (MinIO), support chat (WebSocket), OTP/trusted users, custom Keycloak providers | Java |
| `common`, `autoconfig` | Shared DTOs and auto-configuration starter | Java |
| `paymo-add`, `telegrambot` | Legacy Paymo integration (Java 8, Oracle) and Telegram bot | Java / Python |

⭐ = services I contributed to most (card ≈237 commits, autodebit ≈158, payment ≈117, log-aggregation 52 of 61).

## Tech stack

**Java 21/23 · Kotlin · Spring Boot 3.1–3.4 · Spring Cloud (Gateway, OpenFeign, Consul, Eureka) · Kafka ·
PostgreSQL · MongoDB · Redis · ClickHouse · Keycloak · Liquibase · Quartz · Micrometer/Zipkin ·
Docker · Maven/Gradle**

## Running a service

```bash
cd card
./mvnw spring-boot:run      # Gradle services: ./gradlew bootRun
```

Configuration is externalized — every secret or endpoint is a `${ENV_VAR}` placeholder (see `.env.example`).
Encrypted values (`ENC(...)`) were replaced with `ENC(REDACTED)`; provide your own Jasypt password and values.

## Security & sanitization

This is a sanitized copy of private production code:
- All credentials, Jasypt master keys, encrypted values, tokens, keys and certificates were removed.
- Environment profiles, CI/CD pipelines, infrastructure manifests and Maven `settings.xml` were removed.
- Internal hostnames/IPs, card numbers, national IDs, phone numbers and personal names/e-mails were masked.
- Git history is not included.
