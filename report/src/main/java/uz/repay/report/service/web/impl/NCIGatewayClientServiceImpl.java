package uz.repay.report.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanGraphResponse;
import uz.repay.common.model.enums.ResultState;
import uz.repay.report.client.NCIGatewayFeignClient;
import uz.repay.report.service.web.NCIGatewayClientService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NCIGatewayClientServiceImpl implements NCIGatewayClientService {
    private final NCIGatewayFeignClient client;

    @Override
    public SimpleResponse<ResultData<List<LoanGraphResponse>>> getLoanGraph(String loanExtId) {
        var request = new LoanSimpleRequestDTO(loanExtId);
        log.info("Sending request to nci_gateway for get loan graph method with request: {}", request);
        try {
            var response = client.getLoanGraph(request);
            log.info("Received response from nci_gateway  for get loan graph method with response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a time out error from nci_gateway with get loan graph method: {}", r.getMessage());
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            log.error("Taken a feign error from nci_gateway with get loan graph method: {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            log.error("Taken an internal error from nci_gateway with get loan graph method: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }

}
