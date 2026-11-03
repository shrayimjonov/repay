package uz.repay.card.service.business;

import lombok.NonNull;
import uz.repay.card.model.dto.core.request.card.CardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.card.CardInfoRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardDeleteDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardsRequestDTO;
import uz.repay.card.model.dto.core.request.loan.LoanCardsV2DTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.CardInfoResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.common.model.dto.response.core.*;

import java.util.ArrayList;
import java.util.List;

public interface LoanCardService {

    @Deprecated
    ResultData<LoanCardsResponseDTO<LoanCardsResult>> getLoanCards(@NonNull LoanCardsRequestDTO requestDTO);

    ResultData<List<LoanCardsResult>> getLoanAllCards(Long loanId);

    ResultData<CoreError> deleteCardFromLoan(@NonNull LoanCardDeleteDTO dto);

    @Deprecated
    ResultData<CoreError> approveCardInLoan(@NonNull LoanCardDeleteDTO dto);

    ResultData<CardInfoResponseDTO> getCardInfo(@NonNull CardInfoRequestDTO dto);

    @Deprecated
    ResultData<CardFinderResponseDTO> findLoanCards(@NonNull CardFinderRequestDTO requestDTO);

    ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCardsV2(@NonNull LoanCardsV2DTO requestDTO);
}
