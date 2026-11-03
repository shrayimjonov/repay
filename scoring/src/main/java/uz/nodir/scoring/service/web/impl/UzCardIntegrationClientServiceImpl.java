package uz.nodir.scoring.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.client.UzCardIntegrationFeignClient;
import uz.nodir.scoring.model.dto.huMoGateway.request.HuMoGateCardLimitRequest;
import uz.nodir.scoring.model.dto.huMoGateway.response.HuMoGateCardLimitResponse;
import uz.nodir.scoring.model.dto.uzCard.request.UzCardLimitRequest;
import uz.nodir.scoring.model.dto.uzCard.response.UzCardBaseResponse;
import uz.nodir.scoring.model.dto.uzCard.response.result.CardLimitResult;
import uz.nodir.scoring.service.web.UzCardIntegrationClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UzCardIntegrationClientServiceImpl implements UzCardIntegrationClientService {
    private final UzCardIntegrationFeignClient client;

    @Override
    public SimpleResponse<UzCardBaseResponse<List<CardLimitResult>>> getCardLimits(String token) {
        var request = new UzCardLimitRequest(
                token
        );
        log.info("Sending request to uzCard integration client to method \"get card limits\" with request: {}", request);

        try {
            var response = client.getCardLimits(request);
            log.info("Taken response from uzCard integration client from \"get card limits\" method with response: {}", response.toString());
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a TIME OUT error from uzCard integration client from \"get card limits\" method with message: {}", r.getMessage());
            return new SimpleResponse<>(r.getMessage(), true);
        } catch (FeignException f) {
            log.error("Taken a THIRD PARTY error from uzCard integration client from \"get card limits\" method with message: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), f.getMessage());
        } catch (Exception e) {
            log.error("Taken an INTERNAL error from uzCard integration client from \"get card limits\" method with message: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }
}
