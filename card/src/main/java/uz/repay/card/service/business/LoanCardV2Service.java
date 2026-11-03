package uz.repay.card.service.business;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import uz.repay.card.model.dto.core.request.card.CardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardToggleRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardsV2DTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.ArrayList;
import java.util.List;

public interface LoanCardV2Service {
    ResultData<List<LoanCardsResult>> getLoanAllCards(@NotNull Long loanId);

    ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCards(@NotNull LoanCardsV2DTO requestDTO);

    ResultData<CoreError> deleteOrApproveCard(@NotNull LoanCardToggleRequestDTO requestDTO);

    ResultData<CardFinderResponseDTO> findLoanCards(@NonNull CardFinderRequestDTO requestDTO);
}
