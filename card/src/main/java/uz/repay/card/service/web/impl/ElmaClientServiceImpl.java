package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.ElmaFeignClient;
import uz.repay.card.model.dto.elma.request.ElmaVisaCardFindRequest;
import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;
import uz.repay.card.service.web.ElmaClientService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElmaClientServiceImpl implements ElmaClientService {
    private final ElmaFeignClient client;


    @Override
    public SimpleResponse<ElmaVisaCardFindResponse> getClientCardsByPINFL(ElmaVisaCardFindRequest request) {
        log.info("Sending request to ELMA for take visaCardsByClient with dto: {}", request);
        try {
            var response = client.getClientVisaCardsByCode(request);
            log.info("Received response from ELMA by take visaCardsByClient method with result: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a Time-OUT error while executing visaCardsByClient method with message: {}", r.getMessage());
            return new SimpleResponse<>(r.getMessage(), false);
        } catch (FeignException f) {
            log.error("Taken a ThirdParty error while executing visaCardsByClient method with message: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal error while executing visaCardsByClient method with message: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

}
