package uz.repay.payment.config.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.BranchUtils;
import uz.repay.payment.model.dto.core.pay.reqeuest.ReverseRequestDTO;
import uz.repay.payment.model.dto.service.ReverseRequestScopedContext;
import uz.repay.payment.service.business.component.BankDayChecker;
import uz.repay.payment.service.business.component.CommonHelper;
import uz.repay.payment.service.core.TransactionDAO;
import uz.repay.payment.utils.PaymentUtils;
import uz.repay.payment.utils.TranUtils;

import static uz.repay.payment.utils.TranUtils.validateStatus;

@Lazy
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class ReverseAspect {
    private final BankDayChecker bankDayChecker;
    private final TransactionDAO transactionDAO;
    private final CommonHelper commonHelper;
    private final ReverseRequestScopedContext reverseRequestScopedContext;

    @Pointcut("@annotation(uz.repay.payment.config.aop.ReversePay) && args(reverseRequestDTO,..))")
    public void handleReverseRequest(ReverseRequestDTO reverseRequestDTO) {
    }

    @Around(value = "handleReverseRequest(reverseRequestDTO)", argNames = "pjp,reverseRequestDTO")
    public Object handledReverse(ProceedingJoinPoint pjp, ReverseRequestDTO reverseRequestDTO) throws Throwable {
        log.info("Start checking aspect for reverse request: {}", reverseRequestDTO);

        commonHelper.checkSwitcherIsEnabled(PaymentUtils.SWITCHER_REVERSE);

        long start = System.currentTimeMillis();

        var transaction = transactionDAO.findById(reverseRequestDTO.getTranId());

        reverseRequestScopedContext.setTransaction(transaction);

        log.info("Checking day for reverse/cancel aop aspect for loan: {}", transaction.getLoanExtId());

        if (!bankDayChecker.checkBankDayForAll(transaction.getLoanExtId()))
            return new ResultData<>(ResultState.InvalidRequest, "%s day is closed!".formatted(BranchUtils.getBranchByLoan(transaction.getLoanExtId()).getCode()));


        validateStatus(transaction.getStatus());

        TranUtils.checkTranDate(transaction);


        log.info("Reverse is possible. Reverse checking spent time : {} ms", System.currentTimeMillis() - start);
        return pjp.proceed();
    }


}
