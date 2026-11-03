package uz.repay.payment.model.dto.core.transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public record ExcelReportMeta(
        String generatedBy,
        LocalDateTime requestedAt,
        LocalDate dateFrom,
        LocalDate dateTo,
        boolean periodDefaulted,
        List<String> appliedFilters,
        long totalRows
) {
}
