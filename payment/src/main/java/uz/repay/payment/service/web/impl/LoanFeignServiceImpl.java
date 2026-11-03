package uz.repay.payment.service.web.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.payment.client.LoanFeignClient;
import uz.repay.payment.service.web.LoanFeignService;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanFeignServiceImpl implements LoanFeignService {
    private final LoanFeignClient client;

    @Override
    public SimpleResponse<ResultData<LoanInfoResponse>> getLoanBalance(String loanExtId) {
        log.info("Get loan: {} balance info", loanExtId);
        try {
            var response = client.getLoanBalance(new LoanSimpleRequestDTO(loanExtId));

            log.info("Balance response: {}", response);
            return new SimpleResponse<>(response);
        } catch (Exception e) {
            log.error("Loan balance info throws error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public ResultData<LoanAdditionalResponseDTO> getLoanAdditionalInfo(String loanExtId) {
        var request = new LoanSimpleRequestDTO(loanExtId);
        log.info("Sending request to LOAN SERVICE to get loan additional info with request: {}", request);

        try {
            var response = client.getLoanAdditionalInfo(request);
            log.info("Received response from LOAN SERVICE about loan addition info with response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Taken an error from LOAN SERVICE while executing loan addition info method throws error: {}", e.getMessage());
            return new ResultData<>(500, e.getMessage());
        }
    }
}
