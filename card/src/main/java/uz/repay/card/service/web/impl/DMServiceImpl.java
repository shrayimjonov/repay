package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.DavrMobileFeignClient;
import uz.repay.card.model.dto.dm.response.CardsByPINFLResponse;
import uz.repay.card.model.dto.uzcard.request.CheckCard2PinflRequest;
import uz.repay.card.service.web.DMService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

@Service
@Slf4j
@RequiredArgsConstructor
public class DMServiceImpl implements DMService {
    private final DavrMobileFeignClient client;


    @Override
    public ResultData<CardsByPINFLResponse> getClientCardsByPINFL(String personCode) {
        log.info("Get client: {} cards from Davr Mobile", personCode);
        try {
            var response = client.getClientCardsByPINFL(personCode);

            if (response.getTokens() == null || response.getTokens().isEmpty())
                return new ResultData<>(ResultState.InvalidRequest, "Tokens is empty");

            return new ResultData<>(response);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }
}
