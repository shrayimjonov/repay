package uz.repay.report.service.business;

import lombok.NonNull;
import uz.repay.report.model.dto.report.request.ReportRequestDTO;

public interface ReportService {
    void generateExcelReport(@NonNull ReportRequestDTO requestDTO);
}
