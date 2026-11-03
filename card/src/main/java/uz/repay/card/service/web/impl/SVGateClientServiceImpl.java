package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uz.repay.card.client.SVGateFeignClient;
import uz.repay.card.client.SVGateNinesFeignClient;
import uz.repay.card.client.SVGateSevensFeignClient;
import uz.repay.card.config.condition.SVGateAdapterCondition;
import uz.repay.card.model.dto.svgate.request.SVGateBaseRequest;
import uz.repay.card.model.dto.svgate.request.params.*;
import uz.repay.card.model.dto.svgate.response.SVGateAutoPayCardsResult;
import uz.repay.card.model.dto.uzcard.request.*;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.*;
import uz.repay.card.service.web.UzCardService;
import uz.repay.card.util.SystemTool;
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
@Conditional(SVGateAdapterCondition.class)
public class SVGateClientServiceImpl implements UzCardService {

    private final SVGateFeignClient client;
    private final SVGateSevensFeignClient svGateSevensFeignClient;
    private final SVGateNinesFeignClient svGateNinesFeignClient;

    @Override
    public UzCardBaseResponse<OTPResult> OTPSend(OTPRequest request) {
        log.info("OTP send request to SV-GATE: {}", request);
        try {
            var changedRequest = new SVGateBaseRequest<>(
                    "2.0",
                    "cards.new.otp",
                    SystemTool.requestIdGenerator(),
                    new SVGateOTPSendParam(
                            request
                    )
            );
            var response = svGateNinesFeignClient.OTPSend(changedRequest);
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
            var changedRequest = new SVGateBaseRequest<>(
                    "2.0",
                    "cards.new.verify",
                    SystemTool.requestIdGenerator(),
                    new SVGateOTPVerifyParam(
                            request
                    )
            );
            var response = svGateNinesFeignClient.OTPVerify(changedRequest);
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
            var changedRequest = new SVGateBaseRequest<>(
                    "2.0",
                    "balance.get",
                    SystemTool.requestIdGenerator(),
                    new SVGateCardsGetParam(
                            request
                    )
            );
            var response = client.getCardsBalance(changedRequest);
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
        var changedRequest = new SVGateBaseRequest<>(
                "2.0",
                "cards.get",
                SystemTool.requestIdGenerator(),
                new SVGateCardsGetParam(
                        request
                )
        );
        return client.getCardsInfo(changedRequest);
    }


    @Override
    public UzCardBaseResponse<P2PInfoResult> getP2PInfo(P2PInfoRequest request) {
        log.warn("P2P Info request to SV-GATE: {}", request);
        try {
            var changedRequest = new SVGateBaseRequest<>(
                    "2.0",
                    "p2p.info",
                    SystemTool.requestIdGenerator(),
                    new SVGateP2PInfoParam(
                            request
                    )
            );

            var response = client.getP2PInfo(changedRequest);
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
            var changedRequest = new SVGateBaseRequest<>(
                    "2.0",
                    "card.check.pinfl",
                    SystemTool.requestIdGenerator(),
                    new SVGateCardCheckPINFLParam(
                            request
                    )
            );
            var response = client.checkCardIsBelongToPersonCode(changedRequest);

            log.info("Check card is belong to PINFL response: {}", response);
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
            var changedRequest = new SVGateBaseRequest<>(
                    "2.0",
                    "cards.list.token",
                    SystemTool.requestIdGenerator(),
                    new SVGateCardsByPhoneParam(
                            request
                    )
            );

            var response = client.getCardsByPhone(changedRequest);

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
        try {
            var changedRequest = new SVGateBaseRequest<>(
                    "2.0",
                    "autopay.cards.get",
                    SystemTool.requestIdGenerator(),
                    request
            );
            log.info("Get autoPay cards request to SV-GATE: {}", changedRequest);

            var response = svGateSevensFeignClient.autoPayCardsGet(changedRequest);
            log.info("Get autoPay cards response: {}", response);
            return new ResultData<>(response);
        } catch (RetryableException r) {
            log.error("Taken a time out error while executing \"Get autopay cards\" method with message: {}", r.getMessage());
            return new ResultData<>(ResultState.TimeOutError, r.getMessage());
        } catch (FeignException f) {
            log.error("Taken a third party error while executing \"Get autopay cards\" method with message: {}", f.getMessage());
            return new ResultData<>(ResultState.ThirdPartyError, f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal error while executing \"Get autopay cards\" method with message: {}", e.getMessage());
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<UzCardBaseResponse<CoreError>> autoPaySubscribe(String token, Boolean enable) {
        var subscribeCode = enable ? 1 : 0;

        var request = new SVGateBaseRequest<>(
                "2.0",
                "autopay.cards.subscribe",
                SystemTool.requestIdGenerator(),
                new SVGateAutoPaySubscribeParam(
                        new SVGateAutoPaySubscribeParam.SVGateAutoPaySubscribeCard(
                                token,
                                subscribeCode
                        )
                )
        );
        log.info("Sending SVGate autoPay subscribe request: {}", request);

        try {
            var response = svGateNinesFeignClient.autoPaySubscribe(request);
            log.info("Taken SVGate autoPay subscribe cards response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }


}
