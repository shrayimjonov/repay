package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import uz.repay.card.client.HuMoMWareSlowResponseFeignClient;
import uz.repay.card.client.HuMoMiddlewareFeignClient;
import uz.repay.card.config.condition.HuMoMiddlewareAdapterCondition;
import uz.repay.card.model.dto.huMoMiddleware.request.HuMoMiddlewareBaseRequest;
import uz.repay.card.model.dto.huMoMiddleware.request.params.*;
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
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.card.util.SystemTool;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@Conditional(HuMoMiddlewareAdapterCondition.class)
public class HuMoMiddlewareClientServiceImpl implements HuMoClientService {
    private final HuMoMiddlewareFeignClient client;
    private final HuMoMWareSlowResponseFeignClient slowResponseFeignClient;


    @Override
    public HumoCardInfoResponse getCardInfo(HumoCardInfoRequest request) {
        try {
            var originalRequest = new HuMoMiddlewareBaseRequest<>(
                    SystemTool.requestIdGenerator(),
                    new HuMoCardV3InfoRequestParam(
                            request
                    )
            );
            log.info("huMo Get Card Info request: {}", originalRequest);

            var response = client.getCustomerCardInfo(originalRequest);
            log.info("huMo Card Info Response: {}", response);
            return new HumoCardInfoResponse(response);
        } catch (RetryableException r) {
            throw new TimeOutException(r.getMessage());
        } catch (FeignException f) {
            throw new GatewayException(f.getMessage());
        } catch (Exception e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }


    @Override
    public HumoTokenResponse getCardToken(String pan) {
        try {
            var originalRequest = new HuMoMiddlewareBaseRequest<>(
                    SystemTool.requestIdGenerator(),
                    new HuMoMaskedPanParam(
                            pan
                    )
            );
            log.info("huMo getCardToken request: {}", originalRequest);

            var response = client.getCardToken(originalRequest);
            log.info("huMo getCardToken response: {}", pan);
            if (response.getError() != null)
                throw new GatewayException(response.getError().getMessage());

            return new HumoTokenResponse(response);
        } catch (RetryableException r) {
            throw new TimeOutException(r.getMessage());
        } catch (FeignException f) {
            throw new GatewayException(f.getMessage());
        } catch (Exception e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }


    @Override
    public HumoCardsByPINFLResponse getClientCardsByPINFL(String PINFL) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoPersonCodeParam(
                        PINFL
                )
        );
        log.info("huMo getClientCardsByPINFL request: {}", originalRequest);

        var result = slowResponseFeignClient.getClientCardsByPINFL(originalRequest);

        log.info("huMo get client cards by pinfl response: {}", result);

        return new HumoCardsByPINFLResponse(result);
    }

    @Override
    public SimpleResponse<ResultData<?>> verify(HuMoCardVerifyRequest request) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoOTPSendParam(
                        request
                )
        );
        log.info("Sending huMo card verify request: {}", originalRequest);

        try {

            var response = slowResponseFeignClient.verify(originalRequest);
            log.info("Received huMo card verify response: {}", response);
            return new SimpleResponse<>(ResultData.ok(response));
        } catch (RetryableException r) {
            log.error("huMo card verify method throws error: {}", "TIME OUT " + r.getMessage());
            return new SimpleResponse<>(504, r.getMessage());
        } catch (FeignException f) {
            log.error("huMo card verify method throws error: {}", "FEIGN ERROR " + f.getMessage());
            return new SimpleResponse<>(500, f.getMessage());
        } catch (Exception e) {
            log.error("huMo card verify method throws error: {}", "Back ERROR " + e.getMessage());
            return new SimpleResponse<>(502, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<?>> confirm(HuMoCardConfirmRequest request) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoOTPVerifyParam(
                        request
                )
        );
        log.info("Sending huMo card confirm request: {}", originalRequest);

        try {
            var response = slowResponseFeignClient.confirm(originalRequest);
            log.info("Received huMo card confirm response: {}", response);
            return new SimpleResponse<>(ResultData.ok(response));
        } catch (RetryableException r) {
            log.error("huMo card confirm method throws error: {}", "TIME OUT " + r.getMessage());
            return new SimpleResponse<>(504, r.getMessage());
        } catch (FeignException f) {
            log.error("huMo card confirm method throws error: {}", "FEIGN ERROR " + f.getMessage());
            return new SimpleResponse<>(500, f.getMessage());
        } catch (Exception e) {
            log.error("huMo card confirm method throws error: {}", "Back ERROR " + e.getMessage());
            return new SimpleResponse<>(502, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<HuMoCardBaseResponse<HuMoP2PInfoResult>> p2pInfo(HumoCardInfoRequest request) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoCardV3InfoRequestParam(
                        request
                )
        );
        log.info("Sending huMo p2pInfo request: {}", originalRequest);

        try {
            var response = client.p2pInfo(originalRequest);
            log.info("Received huMo p2pInfo response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("huMo p2pInfo method throws error: {}", "TIME OUT " + r.getMessage());
            return new SimpleResponse<>(504, r.getMessage());
        } catch (FeignException f) {
            log.error("huMo p2pInfo method throws error: {}", "FEIGN ERROR " + f.getMessage());
            return new SimpleResponse<>(500, f.getMessage());
        } catch (Exception e) {
            log.error("huMo p2pInfo method throws error: {}", "Back ERROR " + e.getMessage());
            return new SimpleResponse<>(502, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<HuMoCardBaseResponse<String>> checkCardToPINFL(HuMoCheckCardToPINFLParam request) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                request
        );
        log.info("Sending request to huMo by checkCardToPINFL method with request data: {}", originalRequest);

        try {
            var response = slowResponseFeignClient.checkCardToPINFL(originalRequest);
            log.info("Received response from huMo by checkCardToPINFL method with response data: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a time out error from huMo by checkCardToPINFL method with message: {}", "TIME OUT " + r.getMessage());
            return new SimpleResponse<>(r.getMessage(), true);
        } catch (FeignException f) {
            log.error("Taken a third party error from huMo by checkCardToPINFL method with message: {}", "FEIGN ERROR " + f.getMessage());
            return new SimpleResponse<>(500, f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal error from huMo by checkCardToPINFL method with message: {}", "Back ERROR " + e.getMessage());
            return new SimpleResponse<>(502, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<HuMoCardBaseResponse<HuMoRealPanResult>> getRealPan(HuMoRealPanRequest request) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                request
        );
        log.info("Sending request to huMo by getRealPan method with request data: {}", originalRequest);

        try {
            var response = client.getCardPan(originalRequest);
            log.info("Received response from huMo by getRealPan method with response data: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a time out error from huMo by getRealPan method with message: {}", "TIME OUT " + r.getMessage());
            return new SimpleResponse<>(r.getMessage(), true);
        } catch (FeignException f) {
            log.error("Taken a third party error from huMo by getRealPan method with message: {}", "FEIGN ERROR " + f.getMessage());
            return new SimpleResponse<>(500, f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal error from huMo by getRealPan method with message: {}", "Back ERROR " + e.getMessage());
            return new SimpleResponse<>(502, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<HuMoCardBaseResponse<HuMoCardsByPinflResult>> getClientCardsByPINFLV2(String personCode) {
        var originalRequest = new HuMoMiddlewareBaseRequest<>(
                SystemTool.requestIdGenerator(),
                new HuMoPersonCodeParam(
                        personCode
                )
        );
        log.info("Sending request to huMo by getClientCardsByPINFLV2 method with request data: {}", originalRequest);

        try {
            var response = slowResponseFeignClient.getClientCardsByPINFLV2(originalRequest);
            log.info("Received response from huMo by getClientCardsByPINFLV2 method with response data: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a time out error from huMo by getClientCardsByPINFLV2 method with message: {}", "TIME OUT " + r.getMessage());
            return new SimpleResponse<>(r.getMessage(), true);
        } catch (FeignException f) {
            log.error("Taken a third party error from huMo by getClientCardsByPINFLV2 method with message: {}", "FEIGN ERROR " + f.getMessage());
            return new SimpleResponse<>(500, f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal error from huMo by getClientCardsByPINFLV2 method with message: {}", "Back ERROR " + e.getMessage());
            return new SimpleResponse<>(502, e.getMessage());
        }
    }

}
