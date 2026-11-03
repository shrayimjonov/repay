package uz.repay.payment.service.component;

import org.junit.jupiter.api.Test;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionExcelRow;
import uz.repay.payment.service.component.TransactionExcelSummary.Line;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionExcelSummaryTest {

    private static final LocalDate AUG_31 = LocalDate.of(2026, 8, 31);
    private static final LocalDate DAY_1 = LocalDate.of(2026, 9, 1);
    private static final LocalDate DAY_2 = LocalDate.of(2026, 9, 2);

    @Test
    void should_convert_minor_units_to_major_when_amount_has_tiyin() {
        assertThat(TransactionExcelSummary.toMajor(120_000_020L)).isEqualTo(1_200_000.20d);
        assertThat(TransactionExcelSummary.toMajor(120_000_000L)).isEqualTo(1_200_000d);
        assertThat(TransactionExcelSummary.toMajor(1L)).isEqualTo(0.01d);
        assertThat(TransactionExcelSummary.toMajor(0L)).isZero();
    }

    @Test
    void should_detect_whole_amount_only_when_tiyin_part_is_zero() {
        assertThat(TransactionExcelSummary.isWhole(120_000_000L)).isTrue();
        assertThat(TransactionExcelSummary.isWhole(0L)).isTrue();
        assertThat(TransactionExcelSummary.isWhole(120_000_020L)).isFalse();
        assertThat(TransactionExcelSummary.isWhole(120_000_001L)).isFalse();
        assertThat(TransactionExcelSummary.isWhole(-150L)).isFalse();
    }

    @Test
    void should_label_pay_types_in_russian_and_merge_alerted_types() {
        assertThat(TransactionExcelSummary.labelOf(AddType.AUTO)).isEqualTo("Автосписание");
        assertThat(TransactionExcelSummary.labelOf(AddType.MANUAL)).isEqualTo("Ручное списание");
        assertThat(TransactionExcelSummary.labelOf(AddType.ALERTED)).isEqualTo("Предупрежденные");
        assertThat(TransactionExcelSummary.labelOf(AddType.ALERTED_DM)).isEqualTo("Предупрежденные");
        assertThat(TransactionExcelSummary.labelOf(null)).isNull();
    }

    @Test
    void should_count_alerted_and_alerted_dm_as_one_pay_type_line() {
        var summary = TransactionExcelSummary.from(List.of(
                row(100L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(200L, Currency.UZS, AddType.ALERTED, null, DAY_1),
                row(300L, Currency.UZS, AddType.ALERTED_DM, null, DAY_2),
                row(400L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_2)));

        var lines = summary.of(Currency.UZS).payTypeLines();

        assertThat(lines).extracting(Line::label)
                .containsExactly("Автосписание", "Ручное списание", "Предупрежденные");
        assertThat(lines.get(2).bucket().count()).isEqualTo(2);
        assertThat(lines.get(2).bucket().amountMinor()).isEqualTo(500L);
    }

    @Test
    void should_keep_currencies_apart_when_export_mixes_uzs_and_usd() {
        var summary = TransactionExcelSummary.from(List.of(
                row(100_000L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(2_550L, Currency.USD, AddType.AUTO, null, DAY_1),
                row(50L, Currency.UZS, AddType.AUTO, null, DAY_2)));

        assertThat(summary.currencies()).containsExactly(Currency.UZS, Currency.USD);
        assertThat(summary.isMultiCurrency()).isTrue();
        assertThat(summary.labelsCurrency()).isTrue();
        assertThat(summary.totalCount()).isEqualTo(3);
        assertThat(summary.of(Currency.UZS).total().count()).isEqualTo(2);
        assertThat(summary.of(Currency.UZS).total().amountMinor()).isEqualTo(100_050L);
        assertThat(summary.of(Currency.USD).total().count()).isEqualTo(1);
        assertThat(summary.of(Currency.USD).total().amountMinor()).isEqualTo(2_550L);
    }

    @Test
    void should_treat_row_as_uzs_when_currency_is_null() {
        var summary = TransactionExcelSummary.from(List.of(row(1_000L, null, AddType.AUTO, null, DAY_1)));

        assertThat(summary.currencies()).containsExactly(Currency.UZS);
        assertThat(summary.labelsCurrency()).isFalse();
        assertThat(summary.of(Currency.UZS).total().amountMinor()).isEqualTo(1_000L);
    }

    @Test
    void should_label_currency_when_only_non_uzs_rows_exported() {
        var summary = TransactionExcelSummary.from(List.of(row(1_000L, Currency.USD, AddType.AUTO, null, DAY_1)));

        assertThat(summary.isMultiCurrency()).isFalse();
        assertThat(summary.labelsCurrency()).isTrue();
    }

    @Test
    void should_return_empty_uzs_summary_when_no_rows_exported() {
        var summary = TransactionExcelSummary.from(List.of());

        assertThat(summary.currencies()).containsExactly(Currency.UZS);
        assertThat(summary.totalCount()).isZero();
        assertThat(summary.manualCount()).isZero();
        assertThat(summary.of(Currency.UZS).employeeLines()).isEmpty();
        assertThat(summary.of(Currency.UZS).payTypeLines()).isEmpty();
        assertThat(TransactionExcelSummary.from(null).totalCount()).isZero();
    }

    @Test
    void should_count_row_as_zero_amount_when_amount_is_null() {
        var summary = TransactionExcelSummary.from(List.of(
                row(null, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(500L, Currency.UZS, AddType.AUTO, null, DAY_1)));

        assertThat(summary.of(Currency.UZS).total().count()).isEqualTo(2);
        assertThat(summary.of(Currency.UZS).total().amountMinor()).isEqualTo(500L);
    }

    @Test
    void should_mark_bucket_not_whole_when_any_amount_has_tiyin_even_if_sum_is_whole() {
        var summary = TransactionExcelSummary.from(List.of(
                row(150L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(50L, Currency.UZS, AddType.AUTO, null, DAY_1)));

        var total = summary.of(Currency.UZS).total();

        assertThat(total.amountMinor()).isEqualTo(200L);
        assertThat(TransactionExcelSummary.isWhole(total.amountMinor())).isTrue();
        assertThat(total.allWhole()).isFalse();
    }

    @Test
    void should_order_days_newest_first_when_rows_span_months() {
        var summary = TransactionExcelSummary.from(List.of(
                row(100L, Currency.UZS, AddType.AUTO, null, AUG_31),
                row(100L, Currency.UZS, AddType.AUTO, null, DAY_2),
                row(100L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(100L, Currency.UZS, AddType.AUTO, null, null)));

        var data = summary.of(Currency.UZS);

        assertThat(data.dayLines(120)).extracting(Line::date).containsExactly(DAY_2, DAY_1, AUG_31);
        assertThat(data.dayLines(1)).hasSize(1);
        assertThat(data.total().count()).isEqualTo(4);
    }

    @Test
    void should_group_only_manual_rows_by_employee_largest_amount_first() {
        var summary = TransactionExcelSummary.from(List.of(
                row(10_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_1),
                row(50_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_1),
                row(20_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_2),
                row(900_000L, Currency.UZS, AddType.AUTO, "system", DAY_1),
                row(700_000L, Currency.UZS, AddType.ALERTED_DM, "user@example.com", DAY_1)));

        var lines = summary.of(Currency.UZS).employeeLines();

        assertThat(lines).extracting(Line::label).containsExactly("user@example.com", "user@example.com");
        assertThat(lines.get(1).bucket().count()).isEqualTo(2);
        assertThat(lines.get(1).bucket().amountMinor()).isEqualTo(30_000L);
        assertThat(summary.manualCount()).isEqualTo(3);
        assertThat(summary.of(Currency.UZS).manualTotal().amountMinor()).isEqualTo(80_000L);
    }

    @Test
    void should_break_employee_tie_by_count_then_name_when_amounts_equal() {
        var summary = TransactionExcelSummary.from(List.of(
                row(10_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_1),
                row(10_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_1),
                row(5_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_1),
                row(5_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_1)));

        assertThat(summary.of(Currency.UZS).employeeLines()).extracting(Line::label)
                .containsExactly("user@example.com", "user@example.com", "user@example.com");
    }

    @Test
    void should_put_unknown_employee_last_when_created_by_is_blank() {
        var summary = TransactionExcelSummary.from(List.of(
                row(999_999L, Currency.UZS, AddType.MANUAL, "  ", DAY_1),
                row(100L, Currency.UZS, AddType.MANUAL, null, DAY_1),
                row(1L, Currency.UZS, AddType.MANUAL, " user@example.com ", DAY_1)));

        var lines = summary.of(Currency.UZS).employeeLines();

        assertThat(lines).extracting(Line::label)
                .containsExactly("user@example.com", TransactionExcelSummary.UNKNOWN_EMPLOYEE);
        assertThat(lines.get(1).bucket().count()).isEqualTo(2);
    }

    @Test
    void should_keep_manual_employee_totals_per_currency_when_employee_works_in_two_currencies() {
        var summary = TransactionExcelSummary.from(List.of(
                row(10_000L, Currency.UZS, AddType.MANUAL, "user@example.com", DAY_1),
                row(700L, Currency.USD, AddType.MANUAL, "user@example.com", DAY_1)));

        assertThat(summary.of(Currency.UZS).employeeLines().get(0).bucket().amountMinor()).isEqualTo(10_000L);
        assertThat(summary.of(Currency.USD).employeeLines().get(0).bucket().amountMinor()).isEqualTo(700L);
    }

    private static TransactionExcelRow row(Long amount,
                                           Currency currency,
                                           AddType payType,
                                           String createdBy,
                                           LocalDate createdDate) {
        return new TransactionExcelRow(
                "LOAN-1",
                "CARD OWNER",
                "860012******1234",
                amount,
                currency,
                ResponseStatus.SUCCESS,
                payType,
                "REF-1",
                "T0001",
                createdDate == null ? null : createdDate.atTime(10, 0),
                createdBy,
                null,
                null,
                createdDate,
                1L
        );
    }
}
