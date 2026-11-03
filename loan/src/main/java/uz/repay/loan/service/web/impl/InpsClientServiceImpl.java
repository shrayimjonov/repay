package uz.repay.loan.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.client.InpsFeignClient;
import uz.repay.loan.service.web.InpsFeignService;

@Service
@RequiredArgsConstructor
@Slf4j
public class InpsClientServiceImpl implements InpsFeignService {
    private final InpsFeignClient client;

    @Override
    public ResultData<InpsInfo> getClientDataByPersonCode(String personCode) {
        log.info("Get inps by person code: {}", personCode);
        try {
            var response = client.getDataByPersonCode(personCode);
            log.debug("Inps by person code: {} result is: {}", personCode, response);
            return response;
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }
}
