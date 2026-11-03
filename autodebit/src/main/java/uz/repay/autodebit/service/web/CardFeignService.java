package uz.repay.autodebit.service.web;

import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO;
import uz.repay.autodebit.model.card.request.HumoCardInfoRequestDTO;
import uz.repay.autodebit.model.card.response.HumoCardInfoResponseList;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.response.cardService.EPosResponseDTO;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.core.result.LoanCardsResult;

import java.util.ArrayList;
import java.util.List;

public interface CardFeignService {

    ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCards(LoanCardsRequestDTO requestDTO);

    ResultData<List<EPosResponseDTO>> getAllEPosses();

    ResultData<Boolean> deactivateCard(Long cardId);

    ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCardsV2(LoanCardsRequestDTO requestDTO);

    ResultData<HumoCardInfoResponseList> getHumoCardsInfoByLoanExtIds(HumoCardInfoRequestDTO requestDTO);

    SimpleResponse<ResultData<CardFullInfoResponseDTO>> getCardByPan(CardFullInfoRequestDTO requestDTO);

}
