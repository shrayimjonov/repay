package uz.repay.report.service.business.impl;

import jakarta.servlet.ServletOutputStream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;
import uz.repay.common.utils.HttpServletUtils;
import uz.repay.report.service.business.SalaryService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaryServiceImpl implements SalaryService {


    @Override
    public void generateSalaryReport(@NonNull ResultData<InpsInfo> request) {
        if (request.getResult() == null)
            throw new InvalidDataException("Result object is null");

        if (request.getResult().getInvoices() == null || request.getResult().getInvoices().isEmpty())
            throw new InvalidDataException("Invoices is empty!");

        export(request.getResult());
    }


    public void export(InpsInfo dto) {
        try {
            var response = HttpServletUtils.getCurrentHttpResponse();
            XSSFWorkbook workbook = new XSSFWorkbook();
            var sheet = workbook.createSheet("INPS_report");

            writeHeaderLine(sheet, workbook);
            writeDataLines(workbook, sheet, dto);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException i) {
            throw new GeneralErrorException("INPS Excel reporter throws error: %s".formatted(i.getMessage()));
        }

    }

    private void writeHeaderLine(XSSFSheet sheet, XSSFWorkbook workbook) {

        var columns = List.of(
                "ИД",
                "ИНН",
                "Наименование Организации",
                "Адрес Организации",
                "Период Начисление",
                "Отправленная Дата",
                "Начисление",
                "Чистая Зарплата"
        );

        log.info("Add columns: {}", columns);

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        int i = 0;
        for (String column : columns) {
            var cell = row.createCell(i++);
            cell.setCellStyle(style);
            cell.setCellValue(column);
        }


    }

    private void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, InpsInfo dto) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);


        for (var invoice : dto.getInvoices()) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            row.createCell(columnCount++).setCellValue(rowCount++);
            row.createCell(columnCount++).setCellValue(invoice.getInn());
            row.createCell(columnCount++).setCellValue(invoice.getOrganizationName());
            row.createCell(columnCount++).setCellValue(invoice.getOrganizationAddress());
            row.createCell(columnCount++).setCellValue(invoice.getPeriod());
            row.createCell(columnCount++).setCellValue(invoice.getSendDate());
            row.createCell(columnCount++).setCellValue(invoice.getTotalInvoices());
            row.createCell(columnCount).setCellValue(calculatePercents(invoice.getTotalInvoices()));

        }

        Row row2 = sheet.createRow(22);
        row2.createCell(0).setCellValue("ФИО:");
        row2.createCell(1).setCellValue(dto.getName());

    }

    public double calculatePercents(Double totalInvoice) {
        return totalInvoice - totalInvoice * 12 / 100;
    }
}
