package uz.repay.common.service;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

public interface ExcelReporter<T> {
    /*
     * inside Map has String it means creating workSheet name
     * list is maybe findAll method arguments or like something
    */
    void generate(Map<String, List<T>> map,  List<String> columns, HttpServletResponse response);


}
