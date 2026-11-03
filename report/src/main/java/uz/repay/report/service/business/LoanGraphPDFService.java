package uz.repay.report.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.report.model.dto.report.request.LoanGraphRequestDTO;
import uz.repay.report.model.dto.report.response.LoanGraphResponseDTO;

public interface LoanGraphPDFService {
    ResultData<LoanGraphResponseDTO> generateLoanGraphPDF(@NotNull LoanGraphRequestDTO requestDTO);
}
