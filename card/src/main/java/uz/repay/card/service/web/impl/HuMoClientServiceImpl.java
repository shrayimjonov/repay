package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import uz.repay.card.client.HuMoIntegrationFeignClient;
import uz.repay.card.config.condition.HuMoIntegrationAdapterCondition;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoCheckCardToPINFLParam;
import uz.repay.card.model.dto.huMoMiddleware.request.params.HuMoRealPanRequest;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoCardsByPinflResult;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoRealPanResult;
import uz.repay.card.model.dto.humo.request.HuMoCardConfirmRequest;
import uz.repay.card.model.dto.humo.request.HuMoCardVerifyRequest;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.request.HumoCardsByPINFLRequest;
import uz.repay.card.model.dto.humo.response.HuMoCardBaseResponse;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoCardsByPINFLResponse;
import uz.repay.card.model.dto.humo.response.HumoTokenResponse;
import uz.repay.card.model.dto.humo.response.result.HuMoP2PInfoResult;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

@Service
@RequiredArgsConstructor
@Slf4j
@Conditional(HuMoIntegrationAdapterCondition.class)
public class HuMoClientServiceImpl implements HuMoClientService {

    private final HuMoIntegrationFeignClient client;


    @Override
    public HumoCardInfoResponse getCardInfo(HumoCardInfoRequest request) {
        log.info("Get Card Info request: {}", request);
        try {
            var response = client.getCardInfo(request);
            log.debug("Card Info Response: {}", response);
            return response;
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
        log.info("getCardToken request: {}", pan);
        try {
            var response = client.getCardToken(pan);
            log.info("getCardToken response: {}", pan);
            return response;
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

        var request = new HumoCardsByPINFLRequest(PINFL);
        log.info("getClientCardsByPINFL request: {}", request);

        return client.getClientCardsByPINFL(request);

    }

    @Override
    public SimpleResponse<ResultData<?>> verify(HuMoCardVerifyRequest request) {
        log.info("Sending huMo card verify request: {}", request);

        try {
            var response = client.verify(request);
            log.info("Received huMo card verify response: {}", response);
            return new SimpleResponse<>(response);
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
        log.info("Sending huMo card confirm request: {}", request);

        try {
            var response = client.confirm(request);
            log.info("Received huMo card confirm response: {}", response);
            return new SimpleResponse<>(response);
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
        log.info("Sending huMo p2pInfo request: {}", request);

        try {
            var response = client.p2pInfo(request);
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
        var message = "HuMo Check card to PINFL method not IMPLEMENTED";
        log.error(message);
        return new SimpleResponse<>(ResultState.NOT_IMPLEMENTED.getCode(), message);
    }

    @Override
    public SimpleResponse<HuMoCardBaseResponse<HuMoRealPanResult>> getRealPan(HuMoRealPanRequest request) {
        var message = "HuMo getRealPan method not IMPLEMENTED";
        log.error(message);
        return new SimpleResponse<>(ResultState.NOT_IMPLEMENTED.getCode(), message);
    }

    @Override
    public SimpleResponse<HuMoCardBaseResponse<HuMoCardsByPinflResult>> getClientCardsByPINFLV2(String personCode) {
        var message = "HuMo getClientCardsByPINFLV2 method not IMPLEMENTED";
        log.error(message);
        return new SimpleResponse<>(ResultState.NOT_IMPLEMENTED.getCode(), message);
    }


}
