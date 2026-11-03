package uz.repay.report.service.component;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class ExcelReporter<T> {

    public void generate(Map<String, List<T>> map, List<String> columns, HttpServletResponse response) {
        log.info("Starting report generator!");
        XSSFWorkbook workbook = new XSSFWorkbook();

        log.info("Object name: {}", this.getClass().getGenericSuperclass().getTypeName());
        map.forEach(
                (key, val) ->
                        createList(key, val, workbook, columns)
        );

        try {
            ServletOutputStream ops = response.getOutputStream();
            workbook.write(ops);
            workbook.close();
            ops.close();
            log.info("Report is ready");
        } catch (IOException i) {
            throw new GeneralErrorException("Universal Excel reporter throws error: %s".formatted(i.getMessage()));
        }
    }

    private void createList(String name, List<T> entities, XSSFWorkbook workbook, List<String> columns) {
        log.info("Create sheet with name: {}", name);

        XSSFSheet var2 = workbook.createSheet(name);
        XSSFRow row = var2.createRow(0);
        try {
            generateReportXLS(entities, var2, row, columns);
        } catch (IllegalAccessException | IntrospectionException | NoSuchFieldException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private void generateReportXLS(List<T> entities, XSSFSheet sheet, XSSFRow row, List<String> columns) throws IllegalAccessException, IntrospectionException, NoSuchFieldException {

        log.info("Starting generate report...");
        final AtomicInteger cellIndex = new AtomicInteger(0);
        row.createCell(cellIndex.getAndIncrement()).setCellValue("#");

        for (String column : columns) {
            row.createCell(cellIndex.getAndIncrement()).setCellValue(column);
        }

        log.info("Add cells with names: {}", columns);


        final AtomicInteger dataRowIndex = new AtomicInteger(1);


        log.info("create cell for all started");
        entities
                .forEach(entity -> {

                    cellIndex.set(0);
                    XSSFRow dataRow = sheet.createRow(dataRowIndex.get());
                    dataRow.createCell(cellIndex.getAndIncrement()).setCellValue(dataRowIndex.getAndIncrement());

                    for (String column : columns) {
                        try {
                            dataRow.createCell(cellIndex.getAndIncrement()).setCellValue(getValueByFieldName(column, entity));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }

                });

        log.info("finish create cell");
    }

    private String getValueByFieldName(String column, T entity) throws NoSuchFieldException, IllegalAccessException {
        var generic = entity.getClass();
        Field field = generic.getDeclaredField(column);
        field.setAccessible(true);
        if (field.get(entity) != null)
            return field.get(entity).toString();
        else
            return null;
    }


}
