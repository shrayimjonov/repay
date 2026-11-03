package uz.repay.payment.service.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.stereotype.Component;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.model.enums.Currency;
import uz.repay.payment.model.dto.core.transaction.ExcelReportMeta;
import uz.repay.payment.model.dto.core.transaction.response.result.TransactionExcelRow;
import uz.repay.payment.service.component.TransactionExcelSummary.Bucket;
import uz.repay.payment.service.component.TransactionExcelSummary.CurrencySummary;
import uz.repay.payment.service.component.TransactionExcelSummary.Line;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static uz.repay.payment.service.component.TransactionExcelSummary.isWhole;
import static uz.repay.payment.service.component.TransactionExcelSummary.toMajor;

@Component
@Slf4j
public class TransactionExcelReporter {

    private static final int FLUSH_WINDOW = 500;

    private static final String SHEET_DASHBOARD = "Сводка";
    private static final String SHEET_DATA = "Операции";

    private static final int ROW_TITLE = 0;
    private static final int ROW_INFO = 1;
    private static final int ROW_HEADER = 2;
    private static final int FIRST_DATA_ROW = 3;

    private static final String[] COLUMNS = {
            "Номер кредита",
            "Владелец карты",
            "Карта",
            "Сумма",
            "Валюта",
            "Статус",
            "Тип оплаты",
            "Номер платежа",
            "Терминал",
            "Дата и время",
            "Кто провёл",
            "Примечание",
            "Причина возврата"
    };

    private static final int[] COLUMN_WIDTH_CHARS = {
            17, 30, 21, 17, 10, 23, 18, 17, 14, 22, 26, 45, 30
    };

    private static final int COL_LOAN_EXT_ID = 0;
    private static final int COL_CARD_OWNER = 1;
    private static final int COL_CARD_PAN = 2;
    private static final int COL_AMOUNT = 3;
    private static final int COL_CURRENCY = 4;
    private static final int COL_STATUS = 5;
    private static final int COL_PAY_TYPE = 6;
    private static final int COL_REF_NUM = 7;
    private static final int COL_TERMINAL_ID = 8;
    private static final int COL_INIT_TIME = 9;
    private static final int COL_CREATED_BY = 10;
    private static final int COL_NOTE = 11;
    private static final int COL_REVERSE_REASON = 12;
    private static final int LAST_COLUMN = COLUMNS.length - 1;

    private static final int DASH_LAST_COLUMN = 7;
    private static final int[] DASH_WIDTH_CHARS = {32, 14, 12, 22, 12, 26, 18, 22};

    private static final int TABLE_COL_KEY = 0;
    private static final int TABLE_COL_COUNT = 1;
    private static final int TABLE_COL_COUNT_SHARE = 2;
    private static final int TABLE_COL_AMOUNT = 3;
    private static final int TABLE_COL_AMOUNT_SHARE = 4;
    private static final int TABLE_LAST_COLUMN = TABLE_COL_AMOUNT_SHARE;

    private static final int MAX_CELL_TEXT = 32_767;
    private static final int MAX_TREND_ROWS = 120;

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String NO_VALUE = "—";
    private static final String TOTAL_LABEL = "ИТОГО";

    public byte[] generate(ExcelReportMeta meta, List<TransactionExcelRow> rows) {
        long startedAt = System.currentTimeMillis();
        SXSSFWorkbook workbook = new SXSSFWorkbook(FLUSH_WINDOW);

        try {
            ExcelStyles styles = new ExcelStyles(workbook);
            TransactionExcelSummary summary = TransactionExcelSummary.from(rows);

            SXSSFSheet data = workbook.createSheet(SHEET_DATA);
            writeDataSheet(data, styles, meta, rows, summary);

            SXSSFSheet dashboard = workbook.createSheet(SHEET_DASHBOARD);
            writeDashboardSheet(dashboard, styles, meta, summary);

            workbook.setActiveSheet(0);
            workbook.setSelectedTab(0);

            byte[] content = toByteArray(workbook);

            log.info("Transaction excel built: rows={}, manualRows={}, currencies={}, bytes={}, durationMs={}",
                    rows.size(), summary.manualCount(), summary.currencies(), content.length,
                    System.currentTimeMillis() - startedAt);

            return content;
        } finally {
            release(workbook);
        }
    }

    private void writeDataSheet(SXSSFSheet sheet,
                                ExcelStyles styles,
                                ExcelReportMeta meta,
                                List<TransactionExcelRow> rows,
                                TransactionExcelSummary summary) {
        sheet.setDisplayGridlines(false);
        sheet.setDefaultRowHeightInPoints(16f);

        Row title = sheet.createRow(ROW_TITLE);
        title.setHeightInPoints(30f);
        band(sheet, title, styles.title, "REPAY · УСПЕШНЫЕ ОПЕРАЦИИ", LAST_COLUMN);

        Row info = sheet.createRow(ROW_INFO);
        info.setHeightInPoints(19f);
        band(sheet, info, styles.infoStrip, describeReport(meta), LAST_COLUMN);

        Row header = sheet.createRow(ROW_HEADER);
        header.setHeightInPoints(30f);
        for (int column = 0; column <= LAST_COLUMN; column++) {
            text(header, column, COLUMNS[column], styles.tableHeader);
        }

        int rowIndex = FIRST_DATA_ROW;
        int ordinal = 0;

        for (TransactionExcelRow data : rows) {
            writeDataRow(sheet.createRow(rowIndex++), styles, ++ordinal, data);
        }

        writeTotalRows(sheet, styles, rows.size(), summary);
        layoutDataSheet(sheet, rows.size());
    }

    private void writeDataRow(Row row, ExcelStyles styles, int ordinal, TransactionExcelRow data) {
        boolean banded = ordinal % 2 == 0;
        Long amount = data.getAmount();

        text(row, COL_LOAN_EXT_ID, data.getLoanExtId(), styles.text(banded));
        text(row, COL_CARD_OWNER, data.getCardOwner(), styles.text(banded));
        text(row, COL_CARD_PAN, data.getCardPan(), styles.centered(banded));
        numeric(row, COL_AMOUNT, amount == null ? null : toMajor(amount),
                styles.money(banded, amount == null || isWhole(amount)));
        text(row, COL_CURRENCY, TransactionExcelSummary.currencyOf(data.getCurrency()).name(), styles.centered(banded));
        text(row, COL_STATUS, nameOf(data.getStatus()), styles.status(data.getStatus()));
        text(row, COL_PAY_TYPE, TransactionExcelSummary.labelOf(data.getPayType()), styles.centered(banded));
        text(row, COL_REF_NUM, data.getRefNum(), styles.text(banded));
        text(row, COL_TERMINAL_ID, data.getTerminalId(), styles.centered(banded));
        dateTime(row, COL_INIT_TIME, data.getInitTime(), styles.dateTime(banded));
        text(row, COL_CREATED_BY, data.getCreatedBy(), styles.text(banded));
        text(row, COL_NOTE, data.getNote(), styles.text(banded));
        text(row, COL_REVERSE_REASON, data.getReverseReason(), styles.text(banded));
    }

    private void writeTotalRows(SXSSFSheet sheet, ExcelStyles styles, int rowCount, TransactionExcelSummary summary) {
        if (rowCount == 0) {
            return;
        }

        int rowIndex = FIRST_DATA_ROW + rowCount;

        if (!summary.isMultiCurrency()) {
            Currency currency = summary.currencies().get(0);
            int firstExcelRow = FIRST_DATA_ROW + 1;
            int lastExcelRow = FIRST_DATA_ROW + rowCount;

            Row row = totalRow(sheet, styles, rowIndex, TOTAL_LABEL + currencySuffix(summary, currency));
            formula(row, COL_CARD_OWNER,
                    "SUBTOTAL(103,%s)".formatted(columnRange(COL_STATUS, firstExcelRow, lastExcelRow)),
                    styles.totalCount);
            formula(row, COL_AMOUNT,
                    "SUBTOTAL(109,%s)".formatted(columnRange(COL_AMOUNT, firstExcelRow, lastExcelRow)),
                    styles.totalMoney(summary.of(currency).total().allWhole()));
            return;
        }

        // SUBTOTAL cannot separate currencies, so each currency gets a static total of the whole export.
        for (Currency currency : summary.currencies()) {
            Bucket total = summary.of(currency).total();
            Row row = totalRow(sheet, styles, rowIndex++, TOTAL_LABEL + currencySuffix(summary, currency));

            numeric(row, COL_CARD_OWNER, total.count(), styles.totalCount);
            money(row, COL_AMOUNT, total.amountMinor(), styles.totalMoney(isWhole(total.amountMinor())));
        }
    }

    private Row totalRow(SXSSFSheet sheet, ExcelStyles styles, int rowIndex, String label) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(20f);

        for (int column = 0; column <= LAST_COLUMN; column++) {
            cell(row, column, styles.total);
        }
        text(row, COL_LOAN_EXT_ID, label, styles.total);

        return row;
    }

    private void layoutDataSheet(SXSSFSheet sheet, int rowCount) {
        for (int column = 0; column <= LAST_COLUMN; column++) {
            sheet.setColumnWidth(column, COLUMN_WIDTH_CHARS[column] * 256);
        }

        sheet.createFreezePane(1, FIRST_DATA_ROW);

        int lastFilterRow = rowCount == 0 ? ROW_HEADER : FIRST_DATA_ROW + rowCount - 1;
        sheet.setAutoFilter(new CellRangeAddress(ROW_HEADER, lastFilterRow, 0, LAST_COLUMN));

        if (rowCount > 0) {
            dataBar(sheet, new CellRangeAddress(
                    FIRST_DATA_ROW, FIRST_DATA_ROW + rowCount - 1, COL_AMOUNT, COL_AMOUNT), ExcelStyles.BAR_MONEY);
        }

        preparePrinting(sheet, LAST_COLUMN, ROW_HEADER);
    }

    private void writeDashboardSheet(SXSSFSheet sheet,
                                     ExcelStyles styles,
                                     ExcelReportMeta meta,
                                     TransactionExcelSummary summary) {
        sheet.setDisplayGridlines(false);

        for (int column = 0; column <= DASH_LAST_COLUMN; column++) {
            sheet.setColumnWidth(column, DASH_WIDTH_CHARS[column] * 256);
        }

        int rowIndex = 0;

        Row title = sheet.createRow(rowIndex++);
        title.setHeightInPoints(34f);
        band(sheet, title, styles.title, "REPAY · ОТЧЁТ ПО УСПЕШНЫМ ОПЕРАЦИЯМ", DASH_LAST_COLUMN);

        Row subtitle = sheet.createRow(rowIndex++);
        subtitle.setHeightInPoints(20f);
        band(sheet, subtitle, styles.infoStrip, describePeriod(meta), DASH_LAST_COLUMN);

        rowIndex = spacer(sheet, styles, rowIndex);
        rowIndex = writeProvenance(sheet, styles, meta, rowIndex);
        rowIndex = spacer(sheet, styles, rowIndex);
        rowIndex = writeKeyFigures(sheet, styles, summary, rowIndex);

        for (Currency currency : summary.currencies()) {
            rowIndex = spacer(sheet, styles, rowIndex);
            rowIndex = writeCurrencySections(sheet, styles, rowIndex, currency,
                    summary.of(currency), currencySuffix(summary, currency));
        }

        sheet.createFreezePane(0, 2);
        preparePrinting(sheet, DASH_LAST_COLUMN, -1);
    }

    private int writeCurrencySections(SXSSFSheet sheet,
                                      ExcelStyles styles,
                                      int startRow,
                                      Currency currency,
                                      CurrencySummary data,
                                      String suffix) {
        boolean detailed = currency == Currency.UZS;
        int rowIndex = startRow;

        if (detailed) {
            rowIndex = writeBreakdown(sheet, styles, rowIndex, "ПО ТИПУ ОПЛАТЫ" + suffix, "Тип оплаты",
                    data.payTypeLines(), data.total(), styles.summaryDate);
            rowIndex = spacer(sheet, styles, rowIndex);
        }

        rowIndex = writeBreakdown(sheet, styles, rowIndex, "ПО ДНЯМ" + suffix, "Дата",
                data.dayLines(MAX_TREND_ROWS), data.total(), styles.summaryDate);

        if (detailed) {
            rowIndex = spacer(sheet, styles, rowIndex);
            rowIndex = writeBreakdown(sheet, styles, rowIndex, "РУЧНЫЕ ОПЕРАЦИИ · ПО СОТРУДНИКАМ" + suffix, "Сотрудник",
                    data.employeeLines(), data.manualTotal(), styles.summaryDate);
        }

        return rowIndex;
    }

    private int writeProvenance(SXSSFSheet sheet, ExcelStyles styles, ExcelReportMeta meta, int rowIndex) {
        Row first = sheet.createRow(rowIndex++);
        first.setHeightInPoints(17f);
        text(first, 0, "Кто сформировал", styles.metaLabel);
        text(first, 1, blankToDash(meta.generatedBy()), styles.metaValue);
        merge(sheet, first.getRowNum(), 1, 3);
        text(first, 5, "Дата формирования", styles.metaLabel);
        text(first, 6, TIMESTAMP.format(meta.requestedAt()), styles.metaValue);
        merge(sheet, first.getRowNum(), 6, DASH_LAST_COLUMN);

        Row second = sheet.createRow(rowIndex++);
        second.setHeightInPoints(17f);
        text(second, 0, "Всего строк", styles.metaLabel);
        numeric(second, 1, meta.totalRows(), styles.metaCount);
        merge(sheet, second.getRowNum(), 1, 3);
        text(second, 5, "Фильтры", styles.metaLabel);
        text(second, 6, describeFilters(meta), styles.metaValue);
        merge(sheet, second.getRowNum(), 6, DASH_LAST_COLUMN);

        return rowIndex;
    }

    private int writeKeyFigures(SXSSFSheet sheet, ExcelStyles styles, TransactionExcelSummary summary, int rowIndex) {
        Row section = sheet.createRow(rowIndex++);
        section.setHeightInPoints(20f);
        band(sheet, section, styles.sectionHeader, "ОСНОВНЫЕ ПОКАЗАТЕЛИ", DASH_LAST_COLUMN);

        Row labels = sheet.createRow(rowIndex++);
        labels.setHeightInPoints(16f);
        Row values = sheet.createRow(rowIndex++);
        values.setHeightInPoints(28f);

        List<Tile> tiles = new ArrayList<>();
        tiles.add(new Tile("ВСЕГО ОПЕРАЦИЙ", styles.tileValue, summary.totalCount()));

        for (Currency currency : summary.currencies()) {
            long minor = summary.of(currency).total().amountMinor();
            tiles.add(new Tile("ОБЩАЯ СУММА" + currencySuffix(summary, currency),
                    styles.tileMoney(isWhole(minor)), toMajor(minor)));
        }

        int span = Math.max(1, (DASH_LAST_COLUMN + 1) / tiles.size());

        for (int index = 0; index < tiles.size(); index++) {
            Tile tile = tiles.get(index);
            int from = index * span;
            int to = from + span - 1;

            for (int column = from; column <= to; column++) {
                cell(labels, column, styles.tileLabel);
                cell(values, column, tile.style());
            }

            text(labels, from, tile.label(), styles.tileLabel);
            merge(sheet, labels.getRowNum(), from, to);

            numeric(values, from, tile.value(), tile.style());
            merge(sheet, values.getRowNum(), from, to);
        }

        return rowIndex;
    }

    private int writeBreakdown(SXSSFSheet sheet,
                               ExcelStyles styles,
                               int startRow,
                               String sectionTitle,
                               String keyColumnTitle,
                               List<Line> lines,
                               Bucket shareBase,
                               CellStyle dateStyle) {
        int rowIndex = startRow;

        Row section = sheet.createRow(rowIndex++);
        section.setHeightInPoints(20f);
        band(sheet, section, styles.sectionHeader, sectionTitle, DASH_LAST_COLUMN);

        Row header = sheet.createRow(rowIndex++);
        header.setHeightInPoints(30f);
        text(header, TABLE_COL_KEY, keyColumnTitle, styles.tableHeader);
        text(header, TABLE_COL_COUNT, "Операций", styles.tableHeader);
        text(header, TABLE_COL_COUNT_SHARE, "Доля операций", styles.tableHeader);
        text(header, TABLE_COL_AMOUNT, "Сумма", styles.tableHeader);
        text(header, TABLE_COL_AMOUNT_SHARE, "Доля суммы", styles.tableHeader);

        if (lines.isEmpty()) {
            return emptyRow(sheet, styles, rowIndex);
        }

        int firstDataRow = rowIndex;
        long totalMinor = 0L;

        for (Line line : lines) {
            Bucket bucket = line.bucket();
            Row row = sheet.createRow(rowIndex++);

            if (line.date() != null) {
                dateOnly(row, TABLE_COL_KEY, line.date(), dateStyle);
            } else {
                text(row, TABLE_COL_KEY, line.label(), styles.summaryLabel);
            }

            numeric(row, TABLE_COL_COUNT, bucket.count(), styles.summaryCount);
            numeric(row, TABLE_COL_COUNT_SHARE, share(bucket.count(), shareBase.count()), styles.summaryPercent);
            money(row, TABLE_COL_AMOUNT, bucket.amountMinor(), styles.summaryMoney(isWhole(bucket.amountMinor())));
            numeric(row, TABLE_COL_AMOUNT_SHARE, share(bucket.amountMinor(), shareBase.amountMinor()), styles.summaryPercent);

            totalMinor += bucket.amountMinor();
        }

        int lastDataRow = rowIndex - 1;
        dataBar(sheet, new CellRangeAddress(firstDataRow, lastDataRow, TABLE_COL_COUNT, TABLE_COL_COUNT), ExcelStyles.BAR_COUNT);
        dataBar(sheet, new CellRangeAddress(firstDataRow, lastDataRow, TABLE_COL_AMOUNT, TABLE_COL_AMOUNT), ExcelStyles.BAR_MONEY);

        Row totals = sheet.createRow(rowIndex++);
        totals.setHeightInPoints(20f);
        text(totals, TABLE_COL_KEY, TOTAL_LABEL, styles.total);
        formula(totals, TABLE_COL_COUNT, sumOf(TABLE_COL_COUNT, firstDataRow, lastDataRow), styles.totalCount);
        formula(totals, TABLE_COL_COUNT_SHARE, sumOf(TABLE_COL_COUNT_SHARE, firstDataRow, lastDataRow), styles.totalPercent);
        formula(totals, TABLE_COL_AMOUNT, sumOf(TABLE_COL_AMOUNT, firstDataRow, lastDataRow), styles.totalMoney(isWhole(totalMinor)));
        formula(totals, TABLE_COL_AMOUNT_SHARE, sumOf(TABLE_COL_AMOUNT_SHARE, firstDataRow, lastDataRow), styles.totalPercent);

        return rowIndex;
    }

    private int emptyRow(SXSSFSheet sheet, ExcelStyles styles, int rowIndex) {
        Row empty = sheet.createRow(rowIndex);
        text(empty, TABLE_COL_KEY, NO_VALUE, styles.summaryLabel);

        for (int column = TABLE_COL_KEY + 1; column <= TABLE_LAST_COLUMN; column++) {
            cell(empty, column, styles.summaryLabel);
        }

        return rowIndex + 1;
    }

    private void band(SXSSFSheet sheet, Row row, CellStyle style, String value, int lastColumn) {
        for (int column = 0; column <= lastColumn; column++) {
            cell(row, column, style);
        }
        text(row, 0, value, style);
        merge(sheet, row.getRowNum(), 0, lastColumn);
    }

    private void merge(SXSSFSheet sheet, int rowIndex, int firstColumn, int lastColumn) {
        if (lastColumn > firstColumn) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, firstColumn, lastColumn));
        }
    }

    private int spacer(SXSSFSheet sheet, ExcelStyles styles, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(7f);
        cell(row, 0, styles.spacer);
        return rowIndex + 1;
    }

    private void dataBar(SXSSFSheet sheet, CellRangeAddress range, byte[] colour) {
        SheetConditionalFormatting formatting = sheet.getSheetConditionalFormatting();
        ConditionalFormattingRule rule = formatting.createConditionalFormattingRule(new XSSFColor(colour, null));
        DataBarFormatting bar = rule.getDataBarFormatting();

        bar.setWidthMin(0);
        bar.setWidthMax(90);

        formatting.addConditionalFormatting(new CellRangeAddress[]{range}, rule);
    }

    private void preparePrinting(SXSSFSheet sheet, int lastColumn, int repeatingRow) {
        sheet.setFitToPage(true);
        sheet.setAutobreaks(true);

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        printSetup.setFitWidth((short) 1);
        printSetup.setFitHeight((short) 0);

        sheet.getFooter().setRight("Стр. &P из &N");
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.LeftMargin, 0.3);
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.RightMargin, 0.3);

        if (repeatingRow >= 0) {
            sheet.setRepeatingRows(new CellRangeAddress(repeatingRow, repeatingRow, 0, lastColumn));
        }
    }

    private String describeReport(ExcelReportMeta meta) {
        return "%s   ·   Сформировал: %s   ·   %s   ·   строк: %s   ·   Фильтры: %s".formatted(
                describePeriod(meta),
                blankToDash(meta.generatedBy()),
                TIMESTAMP.format(meta.requestedAt()),
                meta.totalRows(),
                describeFilters(meta));
    }

    private String describePeriod(ExcelReportMeta meta) {
        String period = "%s — %s".formatted(DATE.format(meta.dateFrom()), DATE.format(meta.dateTo()));
        return meta.periodDefaulted() ? period + "  (по умолчанию: текущий месяц)" : period;
    }

    private String describeFilters(ExcelReportMeta meta) {
        List<String> applied = meta.appliedFilters();
        return applied == null || applied.isEmpty() ? NO_VALUE : String.join(";   ", applied);
    }

    private static String currencySuffix(TransactionExcelSummary summary, Currency currency) {
        return summary.labelsCurrency() ? " · " + currency.name() : "";
    }

    private static double share(long part, long whole) {
        return whole == 0 ? 0d : (double) part / whole;
    }

    private static String columnRange(int column, int firstExcelRow, int lastExcelRow) {
        String letter = CellReference.convertNumToColString(column);
        return "%s%d:%s%d".formatted(letter, firstExcelRow, letter, lastExcelRow);
    }

    private static String sumOf(int column, int firstRowIndex, int lastRowIndex) {
        return "SUM(%s)".formatted(columnRange(column, firstRowIndex + 1, lastRowIndex + 1));
    }

    private record Tile(String label, CellStyle style, double value) {
    }

    private static Cell cell(Row row, int column, CellStyle style) {
        Cell cell = row.getCell(column);

        if (cell == null) {
            cell = row.createCell(column);
        }
        cell.setCellStyle(style);

        return cell;
    }

    private static void text(Row row, int column, String value, CellStyle style) {
        Cell cell = cell(row, column, style);

        if (value != null) {
            cell.setCellValue(value.length() > MAX_CELL_TEXT ? value.substring(0, MAX_CELL_TEXT) : value);
        }
    }

    private static void numeric(Row row, int column, Number value, CellStyle style) {
        Cell cell = cell(row, column, style);

        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
    }

    private static void money(Row row, int column, long minor, CellStyle style) {
        numeric(row, column, toMajor(minor), style);
    }

    private static void dateTime(Row row, int column, LocalDateTime value, CellStyle style) {
        Cell cell = cell(row, column, style);

        if (value != null) {
            cell.setCellValue(value);
        }
    }

    private static void dateOnly(Row row, int column, LocalDate value, CellStyle style) {
        Cell cell = cell(row, column, style);

        if (value != null) {
            cell.setCellValue(value);
        }
    }

    private static void formula(Row row, int column, String formula, CellStyle style) {
        cell(row, column, style).setCellFormula(formula);
    }

    private static String nameOf(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private static String blankToDash(String value) {
        return value == null || value.isBlank() ? NO_VALUE : value;
    }

    private static byte[] toByteArray(SXSSFWorkbook workbook) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new GeneralErrorException("Failed to write transactions excel: %s".formatted(e.getMessage()));
        }
    }

    private static void release(SXSSFWorkbook workbook) {
        try {
            workbook.close();
        } catch (IOException e) {
            log.warn("Failed to close transactions excel workbook: {}", e.getMessage());
        } finally {
            workbook.dispose();
        }
    }
}
