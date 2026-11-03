package uz.repay.payment.service.component;

import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Currency;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionExcelRow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

final class TransactionExcelSummary {

    static final String UNKNOWN_EMPLOYEE = "—";

    private static final int MINOR_SCALE = 2;
    private static final long MINOR_PER_MAJOR = 100L;

    private final Map<Currency, CurrencySummary> byCurrency = new EnumMap<>(Currency.class);
    private long totalCount;
    private long manualCount;

    private TransactionExcelSummary() {
    }

    static TransactionExcelSummary from(List<TransactionExcelRow> rows) {
        TransactionExcelSummary summary = new TransactionExcelSummary();

        if (rows != null) {
            rows.forEach(summary::add);
        }

        return summary;
    }

    private void add(TransactionExcelRow row) {
        totalCount++;

        if (row.getPayType() == AddType.MANUAL) {
            manualCount++;
        }

        byCurrency.computeIfAbsent(currencyOf(row.getCurrency()), key -> new CurrencySummary()).add(row);
    }

    List<Currency> currencies() {
        return byCurrency.isEmpty() ? List.of(Currency.UZS) : List.copyOf(byCurrency.keySet());
    }

    CurrencySummary of(Currency currency) {
        CurrencySummary summary = byCurrency.get(currency);
        return summary != null ? summary : new CurrencySummary();
    }

    boolean isMultiCurrency() {
        return byCurrency.size() > 1;
    }

    boolean labelsCurrency() {
        return isMultiCurrency() || byCurrency.keySet().stream().anyMatch(currency -> currency != Currency.UZS);
    }

    long totalCount() {
        return totalCount;
    }

    long manualCount() {
        return manualCount;
    }

    static Currency currencyOf(Currency currency) {
        return currency != null ? currency : Currency.UZS;
    }

    static String employeeOf(String createdBy) {
        return createdBy == null || createdBy.isBlank() ? UNKNOWN_EMPLOYEE : createdBy.trim();
    }

    static String labelOf(AddType payType) {
        PayTypeGroup group = PayTypeGroup.of(payType);
        return group != null ? group.label() : null;
    }

    static boolean isWhole(long minor) {
        return minor % MINOR_PER_MAJOR == 0;
    }

    static double toMajor(long minor) {
        return BigDecimal.valueOf(minor, MINOR_SCALE).doubleValue();
    }

    private static int compareEmployees(String leftName, Bucket left, String rightName, Bucket right) {
        boolean leftUnknown = UNKNOWN_EMPLOYEE.equals(leftName);
        boolean rightUnknown = UNKNOWN_EMPLOYEE.equals(rightName);

        if (leftUnknown != rightUnknown) {
            return leftUnknown ? 1 : -1;
        }

        int byAmount = Long.compare(right.amountMinor(), left.amountMinor());
        if (byAmount != 0) {
            return byAmount;
        }

        int byCount = Long.compare(right.count(), left.count());
        if (byCount != 0) {
            return byCount;
        }

        return leftName.compareTo(rightName);
    }

    enum PayTypeGroup {
        AUTO("Автосписание"),
        MANUAL("Ручное списание"),
        ALERTED("Предупрежденные");

        private final String label;

        PayTypeGroup(String label) {
            this.label = label;
        }

        String label() {
            return label;
        }

        static PayTypeGroup of(AddType payType) {
            if (payType == null) {
                return null;
            }

            return switch (payType) {
                case AUTO -> AUTO;
                case MANUAL -> MANUAL;
                case ALERTED, ALERTED_DM -> ALERTED;
            };
        }
    }

    static final class CurrencySummary {
        private final Bucket total = new Bucket();
        private final Bucket manualTotal = new Bucket();
        private final Map<LocalDate, Bucket> byDay = new TreeMap<>(Comparator.reverseOrder());
        private final Map<PayTypeGroup, Bucket> byPayType = new EnumMap<>(PayTypeGroup.class);
        private final Map<String, Bucket> manualByEmployee = new HashMap<>();

        private void add(TransactionExcelRow row) {
            long minor = row.getAmount() != null ? row.getAmount() : 0L;
            LocalDate date = row.getCreatedDate();
            PayTypeGroup payType = PayTypeGroup.of(row.getPayType());

            total.add(minor);

            if (date != null) {
                byDay.computeIfAbsent(date, key -> new Bucket()).add(minor);
            }
            if (payType != null) {
                byPayType.computeIfAbsent(payType, key -> new Bucket()).add(minor);
            }
            if (payType == PayTypeGroup.MANUAL) {
                manualTotal.add(minor);
                manualByEmployee.computeIfAbsent(employeeOf(row.getCreatedBy()), key -> new Bucket()).add(minor);
            }
        }

        Bucket total() {
            return total;
        }

        Bucket manualTotal() {
            return manualTotal;
        }

        List<Line> dayLines(int limit) {
            return byDay.entrySet()
                    .stream()
                    .limit(limit)
                    .map(entry -> new Line(null, entry.getKey(), entry.getValue()))
                    .toList();
        }

        List<Line> payTypeLines() {
            List<Line> lines = new ArrayList<>(byPayType.size());
            byPayType.forEach((payType, bucket) -> lines.add(new Line(payType.label(), null, bucket)));
            return lines;
        }

        List<Line> employeeLines() {
            return manualByEmployee.entrySet()
                    .stream()
                    .sorted((left, right) -> compareEmployees(left.getKey(), left.getValue(), right.getKey(), right.getValue()))
                    .map(entry -> new Line(entry.getKey(), null, entry.getValue()))
                    .toList();
        }
    }

    static final class Bucket {
        private long count;
        private long amountMinor;
        private boolean allWhole = true;

        private void add(long minor) {
            count++;
            amountMinor += minor;
            allWhole &= isWhole(minor);
        }

        long count() {
            return count;
        }

        long amountMinor() {
            return amountMinor;
        }

        boolean allWhole() {
            return allWhole;
        }
    }

    record Line(String label, LocalDate date, Bucket bucket) {
    }
}
