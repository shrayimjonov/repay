package uz.repay.payment.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.request.card.response.StatusResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.client.CardServiceFeignClient;
import uz.repay.payment.client.UzCardIntegrationFeignClient;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.model.dto.uzcard.request.BalanceRequest;
import uz.repay.payment.model.dto.uzcard.request.GetCardByTokenRequest;
import uz.repay.payment.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.payment.model.dto.uzcard.response.GetCardByTokenResponse;
import uz.repay.payment.model.dto.uzcard.response.result.BalanceResult;
import uz.repay.payment.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.payment.model.dto.uzcard.response.result.P2PInfoResult;
import uz.repay.payment.service.web.CardService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardServiceFeignClient client;
    private final UzCardIntegrationFeignClient uzcardClient;

    @Override
    public SimpleResponse<List<TerminalResponse>> getAllTerminals() {
        log.info("Get terminals request to CARD SERVICE");
        try {
            var response = client.getAllEPosses();
            if (response.getError() != null)
                return new SimpleResponse<>(400, response.getError().getMessage());
            return new SimpleResponse<>(response.getResult());
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<CardFullInfoResponseDTO>> getCardByPan(CardFullInfoRequestDTO requestDTO) {
        log.info("Get card full info request to CARD SERVICE: {}", requestDTO);
        try {
            var response = client.getCardFullInfoByPan(requestDTO);

            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<ResultData<List<StatusResponseDTO>>> findAllStatus() {
        log.info("Get all status request to CARD SERVICE");
        try {
            var response = client.findAllStatus();
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Get all status from Card Service has error: {}", r.getMessage());
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            log.error("Get all status from Card Service has error: {}", f.getMessage());
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            log.error("Get all status from Card Service has error: {}", e.getMessage());
            return new SimpleResponse<>(582, e.getMessage());
        }
    }


    @Override
    public SimpleResponse<P2PInfoResult> getP2PInfo(P2PInfoRequest request) {
        log.info("Get terminals request to CARD SERVICE");
        try {
            var response = uzcardClient.getP2PInfo(request);
            if (response.getError() != null) {
                return new SimpleResponse<>(400, response.getError().getMessage());
            }
            return new SimpleResponse<>(response.getResult());
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<List<CardBaseResult>> getCardInfo(BalanceRequest request) {
        log.info("Get terminals request to CARD SERVICE");
        try {
            var response = uzcardClient.getCardsInfo(request);
            if (response.getError() != null) {
                return new SimpleResponse<>(400, response.getError().getMessage());
            }
            return new SimpleResponse<>(response.getResult());
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<BalanceResult> getCardBalance(BalanceRequest request) {
        log.info("Get terminals request to CARD SERVICE");
        try {
            var response = uzcardClient.getCardsBalance(request);
            if (response.getError() != null) {
                return new SimpleResponse<>(400, response.getError().getMessage());
            }
            return new SimpleResponse<>(response.getResult());
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<GetCardByTokenResponse> getCardByToken(GetCardByTokenRequest request) {
        log.info("Get terminals request to CARD SERVICE");
        try {
            var response = client.getCardByToken(request);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }
}
