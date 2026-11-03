package uz.repay.loan.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.request.client.ClientInfoRequestDTO;
import uz.repay.common.model.dto.request.gateway.gcp.GatewayClientInfoGCPResponse;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.client.ClientServiceFeignClient;
import uz.repay.loan.service.web.ClientService;

import java.time.LocalDate;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientServiceFeignClient client;

    @Override
    public SimpleResponse<ResultData<GatewayClientInfoGCPResponse>> getClientInfo(String personCode, LocalDate birthday) {
        var request = new ClientInfoRequestDTO(personCode, birthday);
        log.info("Sending request to client service to \" get client info \" method with request: {}", request);
        try {
            var response = client.getClientInfo(request);
            log.info("Taken response from client service from method \" get client info \" with response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Taken a TIME OUT error while executing method \" get client info \" with message : {}", r.getMessage());
            return new SimpleResponse<>(ResultState.TimeOutError.getCode(), "nciGateway TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            log.error("Taken a Third Party error while executing method \" get client info \" with message : {}", f.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "nciGateway THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            log.error("Taken an INTERNAL error while executing method \" get client info \" with message : {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), e.getMessage());
        }
    }
}
