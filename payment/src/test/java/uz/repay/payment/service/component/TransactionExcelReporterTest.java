package uz.repay.payment.service.component;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.dto.core.transaction.ExcelReportMeta;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionExcelRow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionExcelReporterTest {

    private static final String SHEET_DATA = "Операции";
    private static final String SHEET_DASH = "Сводка";

    private static final int FIRST_DATA_ROW = 3;
    private static final int COL_LOAN_EXT_ID = 0;
    private static final int COL_CARD_OWNER = 1;
    private static final int COL_AMOUNT = 3;
    private static final int COL_CURRENCY = 4;
    private static final int COL_PAY_TYPE = 6;

    private static final String WHOLE = "#,##0";
    private static final String FRACTION = "#,##0.00";

    private static final LocalDate DAY_1 = LocalDate.of(2026, 9, 1);
    private static final LocalDate DAY_2 = LocalDate.of(2026, 9, 2);

    private static final String EMPLOYEE_A = "user@example.com";
    private static final String EMPLOYEE_B = "user@example.com";

    private static final String SECTION_PAY_TYPE = "ПО ТИПУ ОПЛАТЫ";
    private static final String SECTION_EMPLOYEE = "РУЧНЫЕ ОПЕРАЦИИ · ПО СОТРУДНИКАМ";

    private static final DataFormatter RU = new DataFormatter(Locale.of("ru", "RU"));

    private final TransactionExcelReporter reporter = new TransactionExcelReporter();

    @Test
    void should_render_amount_in_sum_with_two_tiyin_digits_when_amount_has_tiyin() throws IOException {
        try (var workbook = generate(List.of(row(120_000_020L, Currency.UZS, AddType.AUTO, null, DAY_1)))) {
            Cell amount = workbook.getSheet(SHEET_DATA).getRow(FIRST_DATA_ROW).getCell(COL_AMOUNT);

            assertThat(amount.getCellType()).isEqualTo(CellType.NUMERIC);
            assertThat(amount.getNumericCellValue()).isEqualTo(1_200_000.20d);
            assertThat(amount.getCellStyle().getDataFormatString()).isEqualTo(FRACTION);
            assertThat(renderRu(amount)).isEqualTo("1 200 000,20");
        }
    }

    @Test
    void should_render_amount_without_tiyin_part_when_tiyin_is_zero() throws IOException {
        try (var workbook = generate(List.of(row(120_000_000L, Currency.UZS, AddType.AUTO, null, DAY_1)))) {
            Cell amount = workbook.getSheet(SHEET_DATA).getRow(FIRST_DATA_ROW).getCell(COL_AMOUNT);

            assertThat(amount.getNumericCellValue()).isEqualTo(1_200_000d);
            assertThat(amount.getCellStyle().getDataFormatString()).isEqualTo(WHOLE);
            assertThat(renderRu(amount)).isEqualTo("1 200 000");
        }
    }

    @Test
    void should_keep_leading_zero_tiyin_digit_when_amount_is_small() throws IOException {
        try (var workbook = generate(List.of(
                row(120_000_001L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(5L, Currency.UZS, AddType.AUTO, null, DAY_1)))) {
            Sheet data = workbook.getSheet(SHEET_DATA);

            assertThat(renderRu(data.getRow(FIRST_DATA_ROW).getCell(COL_AMOUNT))).isEqualTo("1 200 000,01");
            assertThat(renderRu(data.getRow(FIRST_DATA_ROW + 1).getCell(COL_AMOUNT))).isEqualTo("0,05");
        }
    }

    @Test
    void should_write_uzs_currency_when_row_currency_is_null() throws IOException {
        try (var workbook = generate(List.of(row(1_000L, null, AddType.AUTO, null, DAY_1)))) {
            Cell currency = workbook.getSheet(SHEET_DATA).getRow(FIRST_DATA_ROW).getCell(COL_CURRENCY);

            assertThat(currency.getStringCellValue()).isEqualTo("UZS");
        }
    }

    @Test
    void should_write_russian_pay_type_label_on_data_sheet() throws IOException {
        try (var workbook = generate(List.of(
                row(1_000L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(1_000L, Currency.UZS, AddType.MANUAL, EMPLOYEE_A, DAY_1),
                row(1_000L, Currency.UZS, AddType.ALERTED, null, DAY_1),
                row(1_000L, Currency.UZS, AddType.ALERTED_DM, null, DAY_1)))) {
            Sheet data = workbook.getSheet(SHEET_DATA);

            assertThat(data.getRow(FIRST_DATA_ROW).getCell(COL_PAY_TYPE).getStringCellValue()).isEqualTo("Автосписание");
            assertThat(data.getRow(FIRST_DATA_ROW + 1).getCell(COL_PAY_TYPE).getStringCellValue()).isEqualTo("Ручное списание");
            assertThat(data.getRow(FIRST_DATA_ROW + 2).getCell(COL_PAY_TYPE).getStringCellValue()).isEqualTo("Предупрежденные");
            assertThat(data.getRow(FIRST_DATA_ROW + 3).getCell(COL_PAY_TYPE).getStringCellValue()).isEqualTo("Предупрежденные");
        }
    }

    @Test
    void should_use_filter_aware_subtotals_with_fraction_format_when_single_currency_has_tiyin() throws IOException {
        try (var workbook = generate(List.of(
                row(100_000L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(250L, Currency.UZS, AddType.AUTO, null, DAY_1)))) {
            Row total = workbook.getSheet(SHEET_DATA).getRow(FIRST_DATA_ROW + 2);

            assertThat(total.getCell(COL_LOAN_EXT_ID).getStringCellValue()).isEqualTo("ИТОГО");
            assertThat(total.getCell(COL_CARD_OWNER).getCellFormula()).isEqualTo("SUBTOTAL(103,F4:F5)");
            assertThat(total.getCell(COL_AMOUNT).getCellFormula()).isEqualTo("SUBTOTAL(109,D4:D5)");
            assertThat(total.getCell(COL_AMOUNT).getCellStyle().getDataFormatString()).isEqualTo(FRACTION);
        }
    }

    @Test
    void should_use_whole_format_on_subtotal_when_every_amount_is_whole() throws IOException {
        try (var workbook = generate(List.of(
                row(100_000L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(200L, Currency.UZS, AddType.AUTO, null, DAY_1)))) {
            Row total = workbook.getSheet(SHEET_DATA).getRow(FIRST_DATA_ROW + 2);

            assertThat(total.getCell(COL_AMOUNT).getCellStyle().getDataFormatString()).isEqualTo(WHOLE);
        }
    }

    @Test
    void should_write_static_total_per_currency_when_export_mixes_uzs_and_usd() throws IOException {
        try (var workbook = generate(List.of(
                row(100_000L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(2_550L, Currency.USD, AddType.AUTO, null, DAY_1),
                row(20L, Currency.UZS, AddType.AUTO, null, DAY_1)))) {
            Sheet data = workbook.getSheet(SHEET_DATA);
            Row uzs = data.getRow(FIRST_DATA_ROW + 3);
            Row usd = data.getRow(FIRST_DATA_ROW + 4);

            assertThat(uzs.getCell(COL_LOAN_EXT_ID).getStringCellValue()).isEqualTo("ИТОГО · UZS");
            assertThat(uzs.getCell(COL_CARD_OWNER).getNumericCellValue()).isEqualTo(2d);
            assertThat(uzs.getCell(COL_AMOUNT).getNumericCellValue()).isEqualTo(1_000.20d);
            assertThat(renderRu(uzs.getCell(COL_AMOUNT))).isEqualTo("1 000,20");

            assertThat(usd.getCell(COL_LOAN_EXT_ID).getStringCellValue()).isEqualTo("ИТОГО · USD");
            assertThat(usd.getCell(COL_CARD_OWNER).getNumericCellValue()).isEqualTo(1d);
            assertThat(usd.getCell(COL_AMOUNT).getNumericCellValue()).isEqualTo(25.50d);
        }
    }

    @Test
    void should_show_total_amount_tile_in_sum_when_dashboard_built() throws IOException {
        try (var workbook = generate(List.of(row(120_000_020L, Currency.UZS, AddType.AUTO, null, DAY_1)))) {
            Sheet dash = workbook.getSheet(SHEET_DASH);
            Cell label = findCell(dash, "ОБЩАЯ СУММА");
            Cell value = dash.getRow(label.getRowIndex() + 1).getCell(label.getColumnIndex());

            assertThat(value.getNumericCellValue()).isEqualTo(1_200_000.20d);
            assertThat(renderRu(value)).isEqualTo("1 200 000,20");
        }
    }

    @Test
    void should_not_write_month_and_manual_daily_sections_on_dashboard() throws IOException {
        try (var workbook = generate(manualRows())) {
            List<String> texts = strings(workbook.getSheet(SHEET_DASH));

            assertThat(texts).contains(SECTION_PAY_TYPE, "ПО ДНЯМ", SECTION_EMPLOYEE);
            assertThat(texts).noneMatch(text -> text.startsWith("ПО МЕСЯЦАМ"));
            assertThat(texts).noneMatch(text -> text.contains("ПО ДНЯМ И СОТРУДНИКАМ"));
        }
    }

    @Test
    void should_merge_alerted_types_into_one_row_in_pay_type_section() throws IOException {
        try (var workbook = generate(List.of(
                row(100_000L, Currency.UZS, AddType.AUTO, null, DAY_1),
                row(20_000L, Currency.UZS, AddType.ALERTED, null, DAY_1),
                row(30_050L, Currency.UZS, AddType.ALERTED_DM, null, DAY_2),
                row(10_000L, Currency.UZS, AddType.MANUAL, EMPLOYEE_A, DAY_2)))) {
            Sheet dash = workbook.getSheet(SHEET_DASH);
            int section = rowIndexOf(dash, SECTION_PAY_TYPE);

            assertThat(dash.getRow(section + 2).getCell(0).getStringCellValue()).isEqualTo("Автосписание");
            assertThat(dash.getRow(section + 3).getCell(0).getStringCellValue()).isEqualTo("Ручное списание");

            Row alerted = dash.getRow(section + 4);
            assertThat(alerted.getCell(0).getStringCellValue()).isEqualTo("Предупрежденные");
            assertThat(alerted.getCell(1).getNumericCellValue()).isEqualTo(2d);
            assertThat(alerted.getCell(2).getNumericCellValue()).isEqualTo(2d / 4d);
            assertThat(alerted.getCell(3).getNumericCellValue()).isEqualTo(500.50d);
            assertThat(renderRu(alerted.getCell(3))).isEqualTo("500,50");
            assertThat(alerted.getCell(4).getNumericCellValue()).isEqualTo(50_050d / 160_050d);

            assertThat(dash.getRow(section + 5).getCell(0).getStringCellValue()).isEqualTo("ИТОГО");
        }
    }

    @Test
    void should_not_label_currency_on_dashboard_when_export_is_uzs_only() throws IOException {
        try (var workbook = generate(List.of(row(1_000L, Currency.UZS, AddType.MANUAL, EMPLOYEE_A, DAY_1)))) {
            List<String> texts = strings(workbook.getSheet(SHEET_DASH));

            assertThat(texts).contains("ОБЩАЯ СУММА", "ПО ДНЯМ", SECTION_EMPLOYEE);
            assertThat(texts).noneMatch(text -> text.contains(" · UZS"));
        }
    }

    @Test
    void should_show_only_daily_section_for_usd_when_export_mixes_currencies() throws IOException {
        try (var workbook = generate(List.of(
                row(100_000L, Currency.UZS, AddType.MANUAL, EMPLOYEE_A, DAY_1),
                row(2_550L, Currency.USD, AddType.MANUAL, EMPLOYEE_A, DAY_1)))) {
            List<String> texts = strings(workbook.getSheet(SHEET_DASH));

            assertThat(texts).contains(
                    "ОБЩАЯ СУММА · UZS", "ОБЩАЯ СУММА · USD",
                    SECTION_PAY_TYPE + " · UZS", "ПО ДНЯМ · UZS", SECTION_EMPLOYEE + " · UZS",
                    "ПО ДНЯМ · USD");
            assertThat(texts).doesNotContain(SECTION_PAY_TYPE + " · USD", SECTION_EMPLOYEE + " · USD");
        }
    }

    @Test
    void should_show_only_daily_section_when_export_has_only_usd() throws IOException {
        try (var workbook = generate(List.of(row(2_550L, Currency.USD, AddType.MANUAL, EMPLOYEE_A, DAY_1)))) {
            List<String> texts = strings(workbook.getSheet(SHEET_DASH));

            assertThat(texts).contains("ОБЩАЯ СУММА · USD", "ПО ДНЯМ · USD");
            assertThat(texts).noneMatch(text -> text.startsWith(SECTION_PAY_TYPE) || text.startsWith(SECTION_EMPLOYEE));
        }
    }

    @Test
    void should_write_count_then_count_share_then_sum_then_sum_share_in_breakdown_header() throws IOException {
        try (var workbook = generate(manualRows())) {
            Sheet dash = workbook.getSheet(SHEET_DASH);
            Row header = dash.getRow(rowIndexOf(dash, SECTION_PAY_TYPE) + 1);

            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Тип оплаты");
            assertThat(header.getCell(1).getStringCellValue()).isEqualTo("Операций");
            assertThat(header.getCell(2).getStringCellValue()).isEqualTo("Доля операций");
            assertThat(header.getCell(3).getStringCellValue()).isEqualTo("Сумма");
            assertThat(header.getCell(4).getStringCellValue()).isEqualTo("Доля суммы");
        }
    }

    @Test
    void should_summarize_manual_operations_by_employee_when_manual_rows_exported() throws IOException {
        try (var workbook = generate(manualRows())) {
            Sheet dash = workbook.getSheet(SHEET_DASH);
            int section = rowIndexOf(dash, SECTION_EMPLOYEE);

            Row header = dash.getRow(section + 1);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Сотрудник");

            Row first = dash.getRow(section + 2);
            assertThat(first.getCell(0).getStringCellValue()).isEqualTo(EMPLOYEE_A);
            assertThat(first.getCell(1).getNumericCellValue()).isEqualTo(1d);
            assertThat(first.getCell(2).getNumericCellValue()).isEqualTo(1d / 3d);
            assertThat(first.getCell(3).getNumericCellValue()).isEqualTo(500d);
            assertThat(first.getCell(3).getCellStyle().getDataFormatString()).isEqualTo(WHOLE);
            assertThat(first.getCell(4).getNumericCellValue()).isEqualTo(50_000d / 80_050d);

            Row second = dash.getRow(section + 3);
            assertThat(second.getCell(0).getStringCellValue()).isEqualTo(EMPLOYEE_B);
            assertThat(second.getCell(1).getNumericCellValue()).isEqualTo(2d);
            assertThat(second.getCell(2).getNumericCellValue()).isEqualTo(2d / 3d);
            assertThat(second.getCell(3).getNumericCellValue()).isEqualTo(300.50d);
            assertThat(renderRu(second.getCell(3))).isEqualTo("300,50");
            assertThat(second.getCell(4).getNumericCellValue()).isEqualTo(30_050d / 80_050d);

            Row total = dash.getRow(section + 4);
            int firstExcelRow = section + 3;
            int lastExcelRow = firstExcelRow + 1;
            assertThat(total.getCell(0).getStringCellValue()).isEqualTo("ИТОГО");
            assertThat(total.getCell(1).getCellFormula()).isEqualTo("SUM(B%d:B%d)".formatted(firstExcelRow, lastExcelRow));
            assertThat(total.getCell(2).getCellFormula()).isEqualTo("SUM(C%d:C%d)".formatted(firstExcelRow, lastExcelRow));
            assertThat(total.getCell(3).getCellFormula()).isEqualTo("SUM(D%d:D%d)".formatted(firstExcelRow, lastExcelRow));
            assertThat(total.getCell(3).getCellStyle().getDataFormatString()).isEqualTo(FRACTION);
            assertThat(total.getCell(4).getCellFormula()).isEqualTo("SUM(E%d:E%d)".formatted(firstExcelRow, lastExcelRow));
        }
    }

    @Test
    void should_show_dash_in_employee_section_when_no_manual_rows_exported() throws IOException {
        try (var workbook = generate(List.of(row(1_000L, Currency.UZS, AddType.AUTO, "system", DAY_1)))) {
            Sheet dash = workbook.getSheet(SHEET_DASH);
            int section = rowIndexOf(dash, SECTION_EMPLOYEE);

            assertThat(dash.getRow(section + 2).getCell(0).getStringCellValue()).isEqualTo("—");
        }
    }

    @Test
    void should_build_both_sheets_when_no_rows_exported() throws IOException {
        try (var workbook = generate(List.of())) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(2);
            assertThat(workbook.getSheetName(0)).isEqualTo(SHEET_DATA);
            assertThat(workbook.getSheetName(1)).isEqualTo(SHEET_DASH);

            Sheet dash = workbook.getSheet(SHEET_DASH);
            assertThat(dash.getRow(rowIndexOf(dash, "ПО ДНЯМ") + 2).getCell(0).getStringCellValue()).isEqualTo("—");
        }
    }

    private static List<TransactionExcelRow> manualRows() {
        return List.of(
                row(50_000L, Currency.UZS, AddType.MANUAL, EMPLOYEE_A, DAY_1),
                row(10_000L, Currency.UZS, AddType.MANUAL, EMPLOYEE_B, DAY_1),
                row(20_050L, Currency.UZS, AddType.MANUAL, EMPLOYEE_B, DAY_2),
                row(900_000L, Currency.UZS, AddType.AUTO, null, DAY_1));
    }

    private XSSFWorkbook generate(List<TransactionExcelRow> rows) throws IOException {
        var meta = new ExcelReportMeta(
                "user@example.com",
                LocalDateTime.of(2026, 9, 11, 9, 0),
                DAY_1,
                DAY_2,
                false,
                List.of(),
                rows.size());

        return new XSSFWorkbook(new ByteArrayInputStream(reporter.generate(meta, rows)));
    }

    private static String renderRu(Cell cell) {
        return RU.formatCellValue(cell).replace(' ', ' ').replace(' ', ' ');
    }

    private static int rowIndexOf(Sheet sheet, String text) {
        for (Row row : sheet) {
            Cell first = row.getCell(0);

            if (first != null && first.getCellType() == CellType.STRING && text.equals(first.getStringCellValue())) {
                return row.getRowNum();
            }
        }
        throw new AssertionError("Row not found: " + text);
    }

    private static Cell findCell(Sheet sheet, String text) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING && text.equals(cell.getStringCellValue())) {
                    return cell;
                }
            }
        }
        throw new AssertionError("Cell not found: " + text);
    }

    private static List<String> strings(Sheet sheet) {
        var texts = new ArrayList<String>();

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    texts.add(cell.getStringCellValue());
                }
            }
        }

        return texts;
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
