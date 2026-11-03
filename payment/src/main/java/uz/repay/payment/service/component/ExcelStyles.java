package uz.repay.payment.service.component;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

final class ExcelStyles {

    enum StatusTone {
        GOOD, BAD, WARN, NEUTRAL
    }

    private static final Set<ResponseStatus> IN_FLIGHT = EnumSet.of(
            ResponseStatus.NEW,
            ResponseStatus.CREATED,
            ResponseStatus.PENDING,
            ResponseStatus.CHECKING,
            ResponseStatus.DEBITING,
            ResponseStatus.CREDITING,
            ResponseStatus.REVERSING,
            ResponseStatus.CANCELLING
    );

    private static final String FONT = "Calibri";

    static final String MONEY_WHOLE_FORMAT = "#,##0";
    static final String MONEY_FRACTION_FORMAT = "#,##0.00";
    private static final String COUNT_FORMAT = "#,##0";
    private static final String PERCENT_FORMAT = "0.0%";
    private static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private static final String DATE_FORMAT = "dd.MM.yyyy";

    static final byte[] INK = rgb(0x0F, 0x2A, 0x4A);
    static final byte[] ACCENT = rgb(0x1F, 0x5F, 0xA9);
    static final byte[] HEADER_FILL = rgb(0x23, 0x4E, 0x7D);
    static final byte[] BAND_FILL = rgb(0xF4, 0xF7, 0xFB);
    static final byte[] STRIP_FILL = rgb(0xE8, 0xEF, 0xF7);
    static final byte[] TOTAL_FILL = rgb(0xDC, 0xE6, 0xF2);
    static final byte[] TILE_FILL = rgb(0xF4, 0xF7, 0xFB);
    static final byte[] GRID_LINE = rgb(0xD6, 0xDE, 0xE8);
    static final byte[] MUTED = rgb(0x6B, 0x77, 0x84);
    static final byte[] WHITE = rgb(0xFF, 0xFF, 0xFF);

    static final byte[] BAR_COUNT = rgb(0x5B, 0x9B, 0xD5);
    static final byte[] BAR_MONEY = rgb(0x7F, 0xB8, 0x7F);

    private static final byte[] GOOD_FILL = rgb(0xE3, 0xF5, 0xE9);
    private static final byte[] GOOD_TEXT = rgb(0x1E, 0x7B, 0x45);
    private static final byte[] BAD_FILL = rgb(0xFD, 0xE7, 0xE9);
    private static final byte[] BAD_TEXT = rgb(0xB3, 0x26, 0x1E);
    private static final byte[] WARN_FILL = rgb(0xFF, 0xF4, 0xDA);
    private static final byte[] WARN_TEXT = rgb(0x8A, 0x5A, 0x00);
    private static final byte[] NEUTRAL_FILL = rgb(0xEE, 0xF0, 0xF3);
    private static final byte[] NEUTRAL_TEXT = rgb(0x4A, 0x55, 0x60);

    private final Workbook workbook;

    final CellStyle title;
    final CellStyle infoStrip;
    final CellStyle sectionHeader;
    final CellStyle metaLabel;
    final CellStyle metaValue;
    final CellStyle metaCount;
    final CellStyle tileLabel;
    final CellStyle tileValue;
    final CellStyle tilePercent;
    final CellStyle tableHeader;
    final CellStyle total;
    final CellStyle totalCount;
    final CellStyle totalPercent;
    final CellStyle summaryLabel;
    final CellStyle summaryDate;
    final CellStyle summaryCount;
    final CellStyle summaryPercent;
    final CellStyle spacer;

    private final CellStyle[] text = new CellStyle[2];
    private final CellStyle[] index = new CellStyle[2];
    private final CellStyle[][] money = new CellStyle[2][2];
    private final CellStyle[] dateTime = new CellStyle[2];
    private final CellStyle[] centered = new CellStyle[2];

    private final CellStyle[] tileMoney = new CellStyle[2];
    private final CellStyle[] totalMoney = new CellStyle[2];
    private final CellStyle[] summaryMoney = new CellStyle[2];

    private final Map<StatusTone, CellStyle> statusTones = new EnumMap<>(StatusTone.class);

    ExcelStyles(Workbook workbook) {
        this.workbook = workbook;

        this.title = bannerStyle(INK, 15, WHITE);
        this.infoStrip = stripStyle();
        this.sectionHeader = sectionStyle();
        this.metaLabel = labelStyle();
        this.metaValue = valueStyle(null);
        this.metaCount = valueStyle(COUNT_FORMAT);
        this.tileLabel = tileLabelStyle();
        this.tileValue = tileValueStyle(COUNT_FORMAT, INK);
        this.tilePercent = tileValueStyle(PERCENT_FORMAT, ACCENT);
        this.tableHeader = headerStyle();
        this.spacer = spacerStyle();

        for (int i = 0; i < 2; i++) {
            boolean banded = i == 1;
            this.text[i] = bodyStyle(banded, HorizontalAlignment.LEFT, null);
            this.index[i] = bodyStyle(banded, HorizontalAlignment.CENTER, COUNT_FORMAT);
            this.dateTime[i] = bodyStyle(banded, HorizontalAlignment.CENTER, TIMESTAMP_FORMAT);
            this.centered[i] = bodyStyle(banded, HorizontalAlignment.CENTER, null);

            for (int w = 0; w < 2; w++) {
                this.money[i][w] = bodyStyle(banded, HorizontalAlignment.RIGHT, moneyFormat(w == 1));
            }
        }

        this.statusTones.put(StatusTone.GOOD, chipStyle(GOOD_FILL, GOOD_TEXT));
        this.statusTones.put(StatusTone.BAD, chipStyle(BAD_FILL, BAD_TEXT));
        this.statusTones.put(StatusTone.WARN, chipStyle(WARN_FILL, WARN_TEXT));
        this.statusTones.put(StatusTone.NEUTRAL, chipStyle(NEUTRAL_FILL, NEUTRAL_TEXT));

        this.total = totalStyle(HorizontalAlignment.LEFT, null);
        this.totalCount = totalStyle(HorizontalAlignment.RIGHT, COUNT_FORMAT);
        this.totalPercent = totalStyle(HorizontalAlignment.CENTER, PERCENT_FORMAT);

        this.summaryLabel = bodyStyle(false, HorizontalAlignment.LEFT, null);
        this.summaryDate = bodyStyle(false, HorizontalAlignment.LEFT, DATE_FORMAT);
        this.summaryCount = bodyStyle(false, HorizontalAlignment.RIGHT, COUNT_FORMAT);
        this.summaryPercent = bodyStyle(false, HorizontalAlignment.CENTER, PERCENT_FORMAT);

        for (int w = 0; w < 2; w++) {
            String moneyFormat = moneyFormat(w == 1);
            this.tileMoney[w] = tileValueStyle(moneyFormat, INK);
            this.totalMoney[w] = totalStyle(HorizontalAlignment.RIGHT, moneyFormat);
            this.summaryMoney[w] = bodyStyle(false, HorizontalAlignment.RIGHT, moneyFormat);
        }
    }

    static String moneyFormat(boolean whole) {
        return whole ? MONEY_WHOLE_FORMAT : MONEY_FRACTION_FORMAT;
    }

    CellStyle text(boolean banded) {
        return text[banded ? 1 : 0];
    }

    CellStyle index(boolean banded) {
        return index[banded ? 1 : 0];
    }

    CellStyle money(boolean banded, boolean whole) {
        return money[banded ? 1 : 0][whole ? 1 : 0];
    }

    CellStyle tileMoney(boolean whole) {
        return tileMoney[whole ? 1 : 0];
    }

    CellStyle totalMoney(boolean whole) {
        return totalMoney[whole ? 1 : 0];
    }

    CellStyle summaryMoney(boolean whole) {
        return summaryMoney[whole ? 1 : 0];
    }

    CellStyle dateTime(boolean banded) {
        return dateTime[banded ? 1 : 0];
    }

    CellStyle centered(boolean banded) {
        return centered[banded ? 1 : 0];
    }

    CellStyle status(ResponseStatus status) {
        return statusTones.get(toneOf(status));
    }

    static StatusTone toneOf(ResponseStatus status) {
        if (status == null) {
            return StatusTone.NEUTRAL;
        }

        String name = status.name();

        if (name.contains("SUCCESS")) {
            return StatusTone.GOOD;
        }
        if (name.contains("FAIL") || name.equals(ResponseStatus.WRONG_REQUEST.name())) {
            return StatusTone.BAD;
        }
        if (name.contains("NEED_CONFIRM") || IN_FLIGHT.contains(status)) {
            return StatusTone.WARN;
        }

        return StatusTone.NEUTRAL;
    }

    private CellStyle bannerStyle(byte[] fill, int points, byte[] textColour) {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention((short) 1);
        fill(style, fill);
        style.setFont(font(points, true, textColour));
        return style;
    }

    private CellStyle stripStyle() {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention((short) 1);
        fill(style, STRIP_FILL);
        style.setFont(font(9, false, INK));
        return style;
    }

    private CellStyle sectionStyle() {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention((short) 1);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBottomBorderColor(new XSSFColor(ACCENT, null));
        style.setFont(font(11, true, ACCENT));
        return style;
    }

    private CellStyle labelStyle() {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention((short) 1);
        style.setFont(font(9, true, MUTED));
        return style;
    }

    private CellStyle valueStyle(String dataFormat) {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font(10, false, INK));

        if (dataFormat != null) {
            style.setDataFormat(format(dataFormat));
        }

        return style;
    }

    private CellStyle tileLabelStyle() {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention((short) 1);
        fill(style, TILE_FILL);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        borderColour(style, GRID_LINE);
        style.setFont(font(9, true, MUTED));
        return style;
    }

    private CellStyle tileValueStyle(String dataFormat, byte[] textColour) {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention((short) 1);
        fill(style, TILE_FILL);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        borderColour(style, GRID_LINE);
        style.setFont(font(16, true, textColour));
        style.setDataFormat(format(dataFormat));
        return style;
    }

    private CellStyle headerStyle() {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        fill(style, HEADER_FILL);
        style.setFont(font(10, true, WHITE));
        border(style, BorderStyle.THIN);
        borderColour(style, WHITE);
        return style;
    }

    private CellStyle bodyStyle(boolean banded, HorizontalAlignment alignment, String dataFormat) {
        XSSFCellStyle style = xssf();
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention(alignment == HorizontalAlignment.LEFT ? (short) 1 : (short) 0);
        style.setFont(font(10, false, INK));
        border(style, BorderStyle.THIN);
        borderColour(style, GRID_LINE);

        if (banded) {
            fill(style, BAND_FILL);
        }
        if (dataFormat != null) {
            style.setDataFormat(format(dataFormat));
        }

        return style;
    }

    private CellStyle chipStyle(byte[] fillColour, byte[] textColour) {
        XSSFCellStyle style = xssf();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        fill(style, fillColour);
        style.setFont(font(9, true, textColour));
        border(style, BorderStyle.THIN);
        borderColour(style, GRID_LINE);
        return style;
    }

    private CellStyle totalStyle(HorizontalAlignment alignment, String dataFormat) {
        XSSFCellStyle style = xssf();
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setIndention(alignment == HorizontalAlignment.LEFT ? (short) 1 : (short) 0);
        fill(style, TOTAL_FILL);
        style.setFont(font(10, true, INK));
        style.setBorderTop(BorderStyle.DOUBLE);
        style.setBorderBottom(BorderStyle.THIN);
        style.setTopBorderColor(new XSSFColor(ACCENT, null));
        style.setBottomBorderColor(new XSSFColor(GRID_LINE, null));

        if (dataFormat != null) {
            style.setDataFormat(format(dataFormat));
        }

        return style;
    }

    private CellStyle spacerStyle() {
        XSSFCellStyle style = xssf();
        style.setFont(font(4, false, INK));
        return style;
    }

    private XSSFCellStyle xssf() {
        return (XSSFCellStyle) workbook.createCellStyle();
    }

    private XSSFFont font(int points, boolean bold, byte[] colour) {
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontName(FONT);
        font.setFontHeightInPoints((short) points);
        font.setBold(bold);

        if (colour != null) {
            font.setColor(new XSSFColor(colour, null));
        }

        return font;
    }

    private short format(String pattern) {
        return workbook.createDataFormat().getFormat(pattern);
    }

    private static void fill(XSSFCellStyle style, byte[] colour) {
        style.setFillForegroundColor(new XSSFColor(colour, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private static void border(CellStyle style, BorderStyle borderStyle) {
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
    }

    private static void borderColour(XSSFCellStyle style, byte[] colour) {
        XSSFColor xssfColour = new XSSFColor(colour, null);
        style.setTopBorderColor(xssfColour);
        style.setBottomBorderColor(xssfColour);
        style.setLeftBorderColor(xssfColour);
        style.setRightBorderColor(xssfColour);
    }

    private static byte[] rgb(int red, int green, int blue) {
        return new byte[]{(byte) red, (byte) green, (byte) blue};
    }
}
