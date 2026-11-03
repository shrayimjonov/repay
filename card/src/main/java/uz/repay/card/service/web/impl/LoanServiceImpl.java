package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.LoanFeignClient;
import uz.repay.card.service.web.LoanService;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final LoanFeignClient client;


    @Override
    public SimpleResponse<ResultData<CoreError>> addLoan(String loanExtId) {
        log.info("Create loan with loanExtId: {}", loanExtId);
        try {
            var response = client.addLoan(new LoanSimpleRequestDTO(loanExtId));
            log.info("Loan create with loanExtId response: loanExtId {}  response: {}", loanExtId, response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("Loan create with loanExtId method has TIME OUT error: {}", r.getMessage());
            return new SimpleResponse<>(new ResultData<>(580, r.getMessage()));
        } catch (FeignException f) {
            log.error("Loan create with loanExtId method has FEIGN error: {}", f.getMessage());
            return new SimpleResponse<>(new ResultData<>(581, f.getMessage()));
        } catch (Exception e) {
            log.error("Loan create with loanExtId method has error: {}", e.getMessage());
            return new SimpleResponse<>(new ResultData<>(582, e.getMessage()));
        }
    }
}
