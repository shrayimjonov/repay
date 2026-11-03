package uz.repay.loan.service.business;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.loan.response.LoanSimpleResponseDTO;

import java.util.List;

public interface FeaturedLoansService {
    ResultData<CoreError> create(@NotNull String loanExtID);

    ResultData<List<LoanSimpleResponseDTO>> findUserFeaturedLoans();

    ResultData<List<LoanSimpleResponseDTO>> findUserFeaturedLoansForInternal(@NonNull String username);
}
