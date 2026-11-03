package uz.repay.card.service.web.impl;

import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.VisaFeignClient;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.visa.request.VisaCardInfoRequest;
import uz.repay.card.model.dto.visa.request.VisaCardsInfoRequest;
import uz.repay.card.model.dto.visa.response.VisaBalanceResponse;
import uz.repay.card.model.dto.visa.response.VisaTakeRealPanResponse;
import uz.repay.card.service.web.VisaMiddlewareService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisaMiddlewareServiceImpl implements VisaMiddlewareService {
    private final VisaFeignClient client;

    @Override
    public SimpleResponse<UzCardBaseResponse<List<CardBaseResult>>> getCardsInfo(VisaCardsInfoRequest requestDTO) {
        log.info("Get visa cards info request: {}", requestDTO);
        try {
            var response = client.getCardsInfo(requestDTO);
            log.info("Get visa cards info response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Get visa cards info method has time out error: {}", r.getMessage());
            return new SimpleResponse<>(510, r.getMessage());
        } catch (Exception e) {
            log.error("Get visa cards info method has error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<UzCardBaseResponse<CardBaseResult>> getCardInfo(VisaCardInfoRequest requestDTO) {
        log.info("Get visa card info request: {}", requestDTO);
        try {
            var response = client.getCardInfo(requestDTO);
            log.info("Get visa card info response: {}", response);
            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Get visa card info method has error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<VisaBalanceResponse> getCardBalance(VisaCardInfoRequest requestDTO) {
        log.info("Get visa card balance request: {}", requestDTO);
        try {
            var response = client.getCardBalance(requestDTO);
            log.info("Get visa card balance response: {}", response);
            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Get visa card balance method has error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<VisaTakeRealPanResponse>> getRealPan(String token) {
        log.info("Get visa real pan request by token: {}", token);
        try {
            var response = client.getRealPan(token);
            log.info("Get visa real pan response: {}", response);
            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Get visa real pan method has error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }


}
