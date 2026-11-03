package uz.repay.card.service.web;

import feign.FeignException;
import feign.RetryableException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoCheckCardToPINFLParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoRealPanRequest;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoCardsByPinflResult;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoRealPanResult;
import uz.repay.card.model.dto.humo.request.HuMoCardConfirmRequest;
import uz.repay.card.model.dto.humo.request.HuMoCardVerifyRequest;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HuMoCardBaseResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoCardsByPINFLResponse;
import uz.repay.card.model.dto.humo.response.HumoTokenResponse;
import uz.repay.card.model.dto.humo.response.result.HuMoP2PInfoResult;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;


public interface HuMoClientService {
    @Retryable(retryFor = {
            TimeOutException.class,
            GatewayException.class,
            GeneralErrorException.class
    }, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    HumoCardInfoResponse getCardInfo(HumoCardInfoRequest request);

    @Retryable(retryFor = {
            TimeOutException.class,
            GatewayException.class,
            GeneralErrorException.class
    }, maxAttempts = 4, backoff = @Backoff(delay = 1000))
    HumoTokenResponse getCardToken(String pan);

    @Retryable(retryFor = {
            RetryableException.class,
            FeignException.class
    }, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    @Deprecated
    HumoCardsByPINFLResponse getClientCardsByPINFL(String PINFL);

    SimpleResponse<ResultData<?>> verify(HuMoCardVerifyRequest request);

    SimpleResponse<ResultData<?>> confirm(HuMoCardConfirmRequest request);

    SimpleResponse<HuMoCardBaseResponse<HuMoP2PInfoResult>> p2pInfo(HumoCardInfoRequest request);

    SimpleResponse<HuMoCardBaseResponse<String>> checkCardToPINFL(HuMoCheckCardToPINFLParam request);

    SimpleResponse<HuMoCardBaseResponse<HuMoRealPanResult>> getRealPan(HuMoRealPanRequest request);

    SimpleResponse<HuMoCardBaseResponse<HuMoCardsByPinflResult>> getClientCardsByPINFLV2(String personCode);

}
