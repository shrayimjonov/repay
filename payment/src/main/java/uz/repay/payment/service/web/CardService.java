package uz.repay.payment.service.web;

import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.request.card.response.StatusResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.model.dto.uzcard.request.BalanceRequest;
import uz.repay.payment.model.dto.uzcard.request.GetCardByTokenRequest;
import uz.repay.payment.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.payment.model.dto.uzcard.response.GetCardByTokenResponse;
import uz.repay.payment.model.dto.uzcard.response.result.BalanceResult;
import uz.repay.payment.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.payment.model.dto.uzcard.response.result.P2PInfoResult;

import java.util.List;

public interface CardService {

    SimpleResponse<List<TerminalResponse>> getAllTerminals();

    SimpleResponse<ResultData<CardFullInfoResponseDTO>> getCardByPan(CardFullInfoRequestDTO requestDTO);

    SimpleResponse<ResultData<List<StatusResponseDTO>>> findAllStatus();

    SimpleResponse<P2PInfoResult> getP2PInfo(P2PInfoRequest request);

    SimpleResponse<List<CardBaseResult>> getCardInfo(BalanceRequest request);

    SimpleResponse<BalanceResult> getCardBalance(BalanceRequest request);

    SimpleResponse<GetCardByTokenResponse> getCardByToken(GetCardByTokenRequest request);
}
