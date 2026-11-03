# Design — Transactions Excel report v2 (payment service)

Endpoint: `POST /transaction/excel` (unchanged contract: same request DTO, same xlsx attachment).
Baseline: commits f4dfa5e…fc9d96e on `test-master` (2026-09-10).

## 1. Problem statement

1. Money is displayed wrong. `transaction.amount` is stored in **minor units** (UZS tiyin, USD cents —
   see `ABSServiceImpl` `amount / 100` USD). The report writes the raw `Long` with format `#,##0`,
   so 1 200 000,20 sum is shown as `120 000 020`.
   Required display: `1 200 000,20`; when tiyin part is `00` → `1 200 000` (no `,00`). Two fraction digits max.
2. Calculation defects found while checking:
   - **C2 — summary ≠ data sheet, and branch leak.** Сводка is built from `getDailyTotals` native query that
     filters only `status='SUCCESS'` + dates. It ignores accessed branches, loanExtId/cardPan/refNum/amount
     filters. A branch-restricted user sees bank-wide totals (data exposure) and totals never match the
     Операции sheet.
   - **C3 — mixed currencies.** `DAVR_VISA` → `Currency.USD` (cents). Totals add USD cents to UZS tiyin.
   - **C4 — month label.** "ПО МЕСЯЦАМ" writes the 1st day with `dd.MM.yyyy` (looks like a single day).
   - **C5 — row count formula** counts non-empty `loanExtId` cells; a null loanExtId under-counts.
3. New summaries on Сводка for pay type **MANUAL** (operations done by employees, `created_by` = username):
   - total grouped by employee;
   - daily grouped by employee (date × employee).

## 2. Invariants

- I1: Every number on Сводка is derived from exactly the rows exported to Операции (same filters, same
  branch access, same statuses). No second query.
- I2: Amounts of different currencies are never added together.
- I3: A displayed amount never hides a non-zero tiyin part (whole-number format only when provably whole).
- I4: Integer minor-unit arithmetic (`long`) for all aggregation; conversion to sum units happens once, at
  cell write: `BigDecimal.valueOf(minor, 2).doubleValue()`.

## 3. Architecture / data flow

```
TransactionServiceImpl.generateExcel
  ├─ readRowsWithinCap(...)                      (unchanged: keyset pages, branch-scoped, ≤500K)
  ├─ ExcelReportMeta                             (unchanged)
  └─ TransactionExcelReporter.generate(meta, rows)     ← dailyTotals parameter removed
        ├─ TransactionExcelSummary.from(rows)          ← NEW package-private aggregator (pure, no Spring)
        ├─ writeDataSheet  (money cells in sum units, per-currency totals)
        └─ writeDashboardSheet (per-currency blocks + MANUAL employee tables)
```

Removed (dead after change, added yesterday only for this report):
`TransactionRepository.getDailyTotals`, `TransactionDAO.getDailyTotals` (+ impl), `TransactionDailyTotal`.

### 3.1 Money formatting (ExcelStyles)

- `MONEY_FRACTION_FORMAT = "#,##0.00"`, `MONEY_WHOLE_FORMAT = "#,##0"`.
  Stored format codes are locale-invariant; Excel renders separators from OS regional settings.
  ru-RU / uz-UZ (bank workstations) → `1 200 000,20` and `1 200 000`. (Assumption A1.)
- Value cells: `minor % 100 == 0` → whole style, else fraction style.
- Formula cells (SUBTOTAL / SUM / SUMIFS) and tiles: whole style only if **every** contributing value is
  whole (then any filtered subset is whole too); otherwise fraction style (I3).
- Styles: `money(banded, whole)` (4 body styles), `totalMoney(whole)`, `summaryMoney(whole)`, `tileMoney(whole)`.

Pseudocode:
```
double toSum(long minor)        = BigDecimal.valueOf(minor, 2).doubleValue()
boolean isWhole(long minor)     = minor % 100 == 0
```

### 3.2 TransactionExcelSummary (new)

```
TransactionExcelSummary.from(List<TransactionExcelRow> rows):
  for row:
    currency = row.currency ?: UZS                          (A2)
    minor    = row.amount   ?: 0
    s = perCurrency[currency]  (EnumMap, created lazily)
    s.total.add(minor)
    if row.createdDate != null:
        s.byDay[createdDate].add(minor)
        s.byMonth[YearMonth(createdDate)].add(minor)
    if row.payType != null: s.byPayType[payType].add(minor)
    if row.payType == MANUAL:
        employee = blank(row.createdBy) ? "—" : row.createdBy.trim()
        s.manualTotal.add(minor)
        s.manualByEmployee[employee].add(minor)
        if createdDate != null: s.manualByDayEmployee[(createdDate, employee)].add(minor)

Bucket { long count; long amountMinor; boolean allWhole = true;
         add(minor): count++; amountMinor += minor; allWhole &= isWhole(minor) }
```
- `currencies()` → present currencies, UZS first then enum order; if none → `[UZS]` with empty summary.
- `totalCount()` → across currencies.
- Orderings:
  - byMonth, byDay: date desc (byDay limited to `MAX_TREND_ROWS` = 120, as today).
  - byPayType: enum order.
  - manualByEmployee: amount desc, count desc, employee asc; "—" last.
  - manualByDayEmployee: date desc, then amount desc, employee asc; "—" last within the day.

### 3.3 Операции sheet

- Amount cell = `toSum(minor)` with per-cell whole/fraction style. Data bar unchanged.
- Currency cell = resolved currency name (null → UZS, A2) so it agrees with the totals.
- Totals:
  - one currency present → single "ИТОГО" row (as today):
    count `SUBTOTAL(103, F{first}:F{last})` (status column, never null — C5),
    amount `SUBTOTAL(109, D…)` — filter-aware.
  - several currencies → one row per currency "ИТОГО · USD" with static count and amount computed by
    TransactionExcelSummary (not filter-aware — documented trade-off T2).
  - label carries " · CUR" whenever the export has several currencies or a non-UZS one.
  - money style of each total per I3 (`allWhole` of that currency).

### 3.4 Сводка sheet

```
title / period / provenance                                  (unchanged)
ОСНОВНЫЕ ПОКАЗАТЕЛИ tiles: [ВСЕГО ОПЕРАЦИЙ] + [ОБЩАЯ СУММА (· CUR if >1 currency)] per currency
for currency in currencies():            suffix = (>1 currency) ? " · " + currency : ""
   ПО ТИПУ ОПЛАТЫ{suffix}                Тип оплаты | Операций | Доля операций | Сумма | Доля суммы
   ПО ДНЯМ{suffix}                       Дата       | Операций | Доля операций | Сумма | Доля суммы
   РУЧНЫЕ ОПЕРАЦИИ · ПО СОТРУДНИКАМ{suffix}
                                          Сотрудник  | Операций | Доля операций | Сумма | Доля суммы
                                          (shares relative to MANUAL count / MANUAL amount)
```
- v2.1 (2026-09-11): removed ПО МЕСЯЦАМ and РУЧНЫЕ ОПЕРАЦИИ · ПО ДНЯМ И СОТРУДНИКАМ.
- Pay types shown in Russian; ALERTED and ALERTED_DM merged into one group:
  AUTO = "Автосписание", MANUAL = "Ручное списание", ALERTED + ALERTED_DM = "Предупрежденные"
  (applies to ПО ТИПУ ОПЛАТЫ rows and the Операции "Тип оплаты" column).
- Both shares: Доля операций = count / base count, Доля суммы = amount / base amount (same currency).
- Non-UZS currencies (e.g. USD) show only ПО ДНЯМ · CUR (+ ОБЩАЯ СУММА · CUR tile);
  ПО ТИПУ ОПЛАТЫ and РУЧНЫЕ ОПЕРАЦИИ · ПО СОТРУДНИКАМ are rendered for UZS only.
```
```
- Every table ends with "ИТОГО" (SUM formulas), money style per I3. Empty table → single "—" row.
- Доля denominator = count of that currency (or MANUAL count for the employee table).
- Tile span = 8 columns / number of tiles.
- Column widths `DASH_WIDTH_CHARS = {32, 32, 22, 18, 4, 26, 18, 22}` (usernames are e-mails, ~30 chars).

## 4. Data model / migration

None. No schema change, no new query. One native query removed.

## 5. Failure modes

| # | Failure | Mitigation |
|---|---------|------------|
| F1 | Excel opened on en-US locale shows `1,200,000.20` | A1 documented; fallback option (text cells) listed in Out of scope |
| F2 | Filtered data sheet with tiyin subset under whole format | I3: whole format only when all values whole |
| F3 | `long` overflow of minor sum | 500K rows × realistic max 1e12 tiyin = 5e17 < 9.2e18 |
| F4 | double precision in cell | ≤ 9e15 exact; totals far below |
| F5 | Null currency / createdBy / createdDate / payType | A2 UZS; "—" employee; excluded from date buckets but in totals |
| F6 | Many employees × days | ≤ ~92 days × employees; SXSSF streaming; well under 1M rows |
| F7 | Rolling deploy | Stateless; API contract unchanged |

## 6. Observability

Keep existing `Transaction excel built` log; add `currencies` and `manualRows` counts. No PII (usernames not logged).

## 7. Trade-offs

- T1: Summary computed in Java from loaded rows instead of SQL — rows are already in memory; guarantees I1 and
  fixes branch leak; cost O(rows) trivial vs. POI writing.
- T2: Multi-currency totals on Операции are static values (not filter-aware) — correctness over filter-awareness
  in the rare multi-currency export; single-currency exports keep filter-aware SUBTOTAL.
- T3: Numeric cells + locale formats instead of preformatted text — keeps sort/filter/SUM; relies on A1.

## 8. Assumptions

- A1: Report consumers use ru/uz regional settings (Russian UI of report confirms audience).
- A2: `currency == null` rows are UZS (matches `Currency.getCurrencyByCode` default).
- A3: "employee"/"username" = `transaction.created_by` (auditor username, e.g. `user@example.com`).
- A4: "daily" grouping uses `created_date` (same column as the period filter and yesterday's summary).

## 9. Out of scope

- Forcing separators independent of OS locale (would require text cells → breaks sum/sort/filter).
- Employee full names (not stored in payment service).
- Changes to request/response contract or frontend.
