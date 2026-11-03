package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uz.repay.card.client.UzCardIntegrationFeignClient;
import uz.repay.card.config.condition.UzCardGatewayAdapterCondition;
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPayCardsParam;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.request.*;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.*;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Lazy
@Conditional(UzCardGatewayAdapterCondition.class)
public class UzCardIntegrationClientServiceImpl implements UzCardService {

    private final UzCardIntegrationFeignClient client;

    @Override
    public UzCardBaseResponse<OTPResult> OTPSend(OTPRequest request) {
        log.info("OTP send request to SV-GATE: {}", request);
        try {
            var response = client.OTPSend(request);
            log.info("OTP response: {}", response);
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
    public UzCardBaseResponse<CardBaseResult> verify(VerifyRequest request) {
        log.info("Verify request to SV-GATE: {}", request);
        try {
            var response = client.OTPVerify(request);
            log.info("Verify response: {}", response);
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
    public UzCardBaseResponse<BalanceResult> getCardsBalance(BalanceRequest request) {
        log.warn("Card balance request to SV-GATE: {}", request);
        try {
            var response = client.getCardsBalance(request);
            log.warn("Card balance response: {}", response);
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
    public UzCardBaseResponse<List<CardBaseResult>> getCardsInfo(BalanceRequest request) {
        log.warn("Card Info request to SV-GATE: {}", request);
        return client.getCardsInfo(request);
    }


    @Override
    public UzCardBaseResponse<P2PInfoResult> getP2PInfo(P2PInfoRequest request) {
        log.warn("P2P Info request to SV-GATE: {}", request);
        try {
            var response = client.getP2PInfo(request);
            log.warn("P2P Info response: {}", response);
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
    public ResultData<CoreError> checkCardIsBelongToPersonCode(CheckCard2PinflRequest request) {
        log.info("Check card is belong to PINFL request to SV-GATE: {}", request);
        try {
            var response = client.checkCardIsBelongToPersonCode(request);

            if (response.getError() != null)
                return new ResultData<>(ResultState.InvalidRequest, response.getError().getMessage());

            return new ResultData<>(response.getResult());
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }


    @Override
    public ResultData<UzCardBaseResponse<List<CardsListToken>>> getCardsByPhone(CheckCardsByPhoneRequest request) {
        log.info("Check card by phone request to SV-GATE: {}", request);
        try {
            var response = client.getCardsByPhone(request);

            return new ResultData<>(response);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<UzCardBaseResponse<List<SVGateAutoPayCardsResult>>> getAutoPayCards(SVGateAutoPayCardsParam request) {
        log.error("NOT IMPLEMENTED get autoPay cards in uzCard Integration service");
        return new ResultData<>(6023, ResultState.NotProcessed.getMessage());
    }

    @Override
    public SimpleResponse<UzCardBaseResponse<CoreError>> autoPaySubscribe(String token, Boolean enable) {
        log.error("NOT IMPLEMENTED get autoPay cards in uzCard Integration service");
        return new SimpleResponse<>(6023, ResultState.NotProcessed.getMessage());
    }
}
