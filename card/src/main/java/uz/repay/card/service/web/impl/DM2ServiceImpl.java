package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.DavrMobile2FeignClient;
import uz.repay.card.model.dto.dm2.response.DM2CardsByPinflResponse;
import uz.repay.card.service.web.DM2Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DM2ServiceImpl implements DM2Service {
    private final DavrMobile2FeignClient client;


    @Override
    public ResultData<List<DM2CardsByPinflResponse>> getClientCardsByPINFL(String personCode) {
        log.info("Get client: {} cards from Davr Mobile 2.0", personCode);
        try {
            var response = client.getClientCardsByPINFL(personCode);
            log.info("Davr Mobile 2.0 Get cards by pinfl response: {}", response);
            return new ResultData<>(response);
        } catch (RetryableException r) {
            log.error("Davr Mobile 2.0 Get cards by pinfl has error: {}", r.getMessage());
            return new ResultData<>(ResultState.TimeOutError, r.getMessage());
        } catch (FeignException f) {
            log.error("Davr Mobile 2.0 Get cards by pinfl has error: {}", f.getMessage());
            return new ResultData<>(ResultState.ThirdPartyError, f.getMessage());
        } catch (Exception e) {
            log.error("Davr Mobile 2.0 Get cards by pinfl has error: {}", e.getMessage());
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

}
