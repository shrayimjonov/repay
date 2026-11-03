package uz.repay.report.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;
import uz.repay.report.client.LoanFeignClient;
import uz.repay.report.model.dto.debtLoan.response.DebtLoanResponse;
import uz.repay.report.model.dto.loan.request.LoanFilterRequest;
import uz.repay.report.model.dto.loan.response.LoanSimpleResponse;
import uz.repay.report.service.web.LoanClientService;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanClientServiceImpl implements LoanClientService {
    private final LoanFeignClient client;

    @Override
    @Deprecated
    public ResultData<List<LoanSimpleResponse>> getAllLoans(LoanSearchParams params) {
        log.info("Get all loans");
        try {
            var response = client.getAllLoans(
                    new LoanFilterRequest(params));
            if (response.getError() != null) {
                throw new GeneralErrorException(response.getError().getMessage());
            }
            return response;
        } catch (RetryableException r) {
            throw new TimeOutException(r.getMessage());
        } catch (FeignException f) {
            throw new GatewayException(f.getMessage());
        } catch (Exception e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    @Override
    public ResultData<List<DebtLoanResponse>> getAllDebtLoans() {
        log.info("Get all Debt Loans");
        try {
            var response = client.getAllDebtLoans();
            if (response.getError() != null)
                throw new GeneralErrorException(response.getError().getMessage());
            return response;
        } catch (RetryableException r) {
            throw new TimeOutException(r.getMessage());
        } catch (FeignException f) {
            throw new GatewayException(f.getMessage());
        } catch (Exception e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }
}
