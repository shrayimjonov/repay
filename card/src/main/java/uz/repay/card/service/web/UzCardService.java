package uz.repay.card.service.web;

import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPayCardsParam;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.request.*;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.*;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

public interface UzCardService {
    UzCardBaseResponse<OTPResult> OTPSend(OTPRequest request);

    UzCardBaseResponse<CardBaseResult> verify(VerifyRequest request);

    @Retryable(retryFor = {
            TimeOutException.class,
            GatewayException.class,
            GeneralErrorException.class
    }, maxAttempts = 2, backoff = @Backoff(delay = 500))
    UzCardBaseResponse<BalanceResult> getCardsBalance(BalanceRequest request);

    UzCardBaseResponse<List<CardBaseResult>> getCardsInfo(BalanceRequest request);

    @Retryable(retryFor = {
            RetryException.class
    }, maxAttempts = 2, backoff = @Backoff(delay = 500))
    UzCardBaseResponse<P2PInfoResult> getP2PInfo(P2PInfoRequest request);

    ResultData<CoreError> checkCardIsBelongToPersonCode(CheckCard2PinflRequest request);

    ResultData<UzCardBaseResponse<List<CardsListToken>>> getCardsByPhone(CheckCardsByPhoneRequest request);

    ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> getAutoPayCards(SVGateAutoPayCardsParam request);

    SimpleResponse<UzCardBaseResponse<CoreError>> autoPaySubscribe(String token, Boolean enable);
}
