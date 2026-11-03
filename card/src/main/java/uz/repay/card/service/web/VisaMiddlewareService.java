package uz.repay.card.service.web;

import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.visa.request.VisaCardInfoRequest;
import uz.repay.card.model.dto.visa.request.VisaCardsInfoRequest;
import uz.repay.card.model.dto.visa.response.VisaBalanceResponse;
import uz.repay.card.model.dto.visa.response.VisaTakeRealPanResponse;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

public interface VisaMiddlewareService {

    SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsInfo(VisaCardsInfoRequest requestDTO);

    SimpleResponse<UzCardBaseResponse<CardBaseResult>> getCardInfo(VisaCardInfoRequest requestDTO);

    SimpleResponse<VisaBalanceResponse> getCardBalance(VisaCardInfoRequest requestDTO);

    SimpleResponse<ResultData<VisaTakeRealPanResponse>> getRealPan(String token);
}
