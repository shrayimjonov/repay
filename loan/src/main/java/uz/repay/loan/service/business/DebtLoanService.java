package uz.repay.loan.service.business;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.debtLoan.request.DebtLoansRequestDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoanDailyReportDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoansPageResponseDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoansResponseDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.result.DebtLoansResult;

import java.time.LocalDate;
import java.util.List;

public interface DebtLoanService {
    ResultData<DebtLoansResponseDTO> findAll(@NonNull DebtLoansRequestDTO requestDTO);

    @Deprecated
    ResultData<List<DebtLoansResult>> getAll();

    ResultData<CoreError> evictCache();

    ResultData<Boolean> deleteById(@NotNull Long id);

    ResultData<List<DebtLoanDailyReportDTO>> findDailyReport();

    ResultData<CoreError> deleteByLoanExtId(@NotNull String loanExtId);

    ResultData<byte[]> getAllV2();

    ResultData<DebtLoansPageResponseDTO> getAllV2Page(int limit, int pageNumber);

    ResultData<List<DebtLoanDailyReportDTO>> findDailyReportByDate(@NotNull LocalDate date);
}
