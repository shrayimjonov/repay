package uz.repay.payment.config.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.BranchUtils;
import uz.repay.payment.model.dto.core.fraud.FraudEventDTO;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;
import uz.repay.payment.model.dto.service.PayRequestScopedContext;
import uz.repay.payment.model.entity.Temporary;
import uz.repay.payment.model.property.InternalProperty;
import uz.repay.payment.redis.entity.Fraud;
import uz.repay.payment.service.business.TemporaryService;
import uz.repay.payment.service.business.component.BankDayChecker;
import uz.repay.payment.service.business.component.CommonHelper;
import uz.repay.payment.service.core.FraudRedisService;
import uz.repay.payment.service.web.LoanFeignService;
import uz.repay.payment.utils.ErrorBuilder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Lazy
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class FraudAspect {
    private final FraudRedisService fraudRedisService;
    private final InternalProperty property;
    private final BankDayChecker bankDayChecker;
    private final TemporaryService temporaryService;
    private final LoanFeignService loanFeignService;
    private final PayRequestScopedContext payRequestScopedContext;
    private final ApplicationEventPublisher eventPublisher;
    private final CommonHelper commonHelper;

    @Pointcut("@annotation(uz.repay.payment.config.aop.IsFraud) && args(payRequestDTO,..))")
    public void handlePayRequest4Fraud(PayRequestDTO payRequestDTO) {
    }

    @Around(value = "handlePayRequest4Fraud(payRequestDTO)", argNames = "pjp,payRequestDTO")
    public Object handledPay(ProceedingJoinPoint pjp, PayRequestDTO payRequestDTO) throws Throwable {
        log.info("Start checking aspect for \"payment\" request: {}", payRequestDTO);

        commonHelper.checkSwitcherIsEnabled();

        if (!bankDayChecker.checkBankDay(payRequestDTO.getDebitParams().getProcessing(), payRequestDTO.getLoanExtId()))
            return new ResultData<>(ResultState.InvalidRequest, "%s day is closed!".formatted(BranchUtils.getBranchByLoan(payRequestDTO.getLoanExtId()).getCode()));


        long start = System.currentTimeMillis();
        log.info("Checking the pay request for fraudulent nature...");

        var result = checkTemporaryTransactions(payRequestDTO);

        if (!result.getStatus())
            return new ResultData<>(result.getError().getCode(), result.getError().getMessage());


        if (!filter(payRequestDTO)) {
            // If fraud detected, return ERROR and message
            log.info("Detected fraud! Fraud spent time : {}", System.currentTimeMillis() - start);
            return new ResultData<>(ResultState.InvalidRequest, "Transaction is Fraudulent!");
        }


        // If fraud disabled or fraud not detected continue transaction
        log.info("Transaction is possible. Fraud spent time : {}", System.currentTimeMillis() - start);
        return pjp.proceed();
    }

    private synchronized Boolean filter(PayRequestDTO payRequestDTO) {

        var fraudList = fraudRedisService.findByLoanExtId(payRequestDTO.getLoanExtId());

        if (!loanListSizeMoreThanNV2(
                payRequestDTO.getLoanExtId(),
                payRequestDTO.getParams().getCardPan(),
                fraudList)
        ) {
            log.info("Loan today used {} times. Please try again later!", property.getFraud().getPayCount());
            return false;
        }


        fraudList
                .sort(
                        Comparator.comparing(Fraud::getLocalDateTime)
                );

        if (!fraudList.isEmpty()) {
            Fraud last = fraudList.getLast();
            if (last.getLocalDateTime().isAfter(LocalDateTime.now().minusSeconds(60)) &&
                    last.getAmount().equals(payRequestDTO.getAmount()) &&
                    last.getPan().equals(payRequestDTO.getParams().getCardPan()))
                return false;
            else {
                return putToMapAndList(payRequestDTO);
            }
        } else {
            return putToMapAndList(payRequestDTO);
        }

    }

    private boolean loanListSizeMoreThanN(String loanId, List<Fraud> fraudList) {
        var loanSize = fraudList
                .stream()
                .map(z -> z.getLoanId().equals(loanId))
                .toList();

        return loanSize.size() < property.getFraud().getPayCount(); // N = 2
    }

    private boolean putToMapAndList(PayRequestDTO requestDTO) {
        var preparedFraud = new Fraud(
                requestDTO
        );
        fraudRedisService.save(preparedFraud);
        return true;
    }

    private boolean loanListSizeMoreThanNV2(String loanId,
                                            String pan,
                                            List<Fraud> fraudList) {

        var frauds = fraudList
                .stream()
                .filter(z -> z.getPan().equals(pan))
                .toList();

        log.info("Loan: {} fraud size: {} for card: {}", loanId, frauds.size(), pan);

        return frauds.size() < property.getFraud().getPayCount(); // N = 2
    }

    private SimpleResponse<Boolean> checkTemporaryTransactions(PayRequestDTO requestDTO) {
        var loanBalanceResponse = loanFeignService.getLoanAdditionalInfo(requestDTO.getLoanExtId());

        if (loanBalanceResponse.getError() != null) {
            var errorMessage = "Can't fetch loan balance for loan: %s. Cause: %s".formatted(
                    requestDTO.getLoanExtId(),
                    loanBalanceResponse.getError().getMessage());

            return new SimpleResponse<>(
                    601,
                    errorMessage
            );
        }


        var loanInfo = loanBalanceResponse.getResult();

        if (loanInfo.getCurrency() == null) {
            var message = "Currency is null!";
            log.error("Method finished with error: {}", message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        if (!loanInfo.getCurrency().equals(Currency.UZS)) {
            var message = "Currency must be UZS! Now: %s".formatted(loanInfo.getCurrency());
            log.error("Method finished with error: {}", message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        payRequestScopedContext.setLoanAdditionalResponseDTO(loanInfo);


        if (loanInfo.getIsInsolvent()) {
            log.info("Loan is insolvent and should be debit all amount!");
            return new SimpleResponse<>(true);
        }


        var loanList = temporaryService.findByLoanExtId(requestDTO.getLoanExtId());

        if (loanList.isEmpty()) {
            String errorMessage = "Loan: %s temporaries not found. Transaction to be continued".formatted(requestDTO.getLoanExtId());
            log.info(errorMessage);

            return new SimpleResponse<>(true);
        }

        var temporariesAmount = loanList
                .stream()
                .mapToLong(Temporary::getAmount)
                .sum();


        var shouldBeDebtAmount = loanInfo.getRecommendedAmount() - temporariesAmount;

        shouldBeDebtAmount = loanInfo.getRecommendedAmount() == 0 ? 0 : shouldBeDebtAmount;

        log.info("Should be taken amount from card: {}", shouldBeDebtAmount);


        if (requestDTO.getAmount() > shouldBeDebtAmount) {
            var errorMessage = "Should be debt amount: %d but repaid amount is: %d".formatted(shouldBeDebtAmount, requestDTO.getAmount());
            log.info(errorMessage);
            return new SimpleResponse<>(
                    603,
                    "Already has temporary payment for loan: %s. Please wait handling!".formatted(requestDTO.getLoanExtId())
            );
        }


        return new SimpleResponse<>(true);
    }


    @AfterReturning(
            pointcut = "(execution(* uz.repay.payment.service.business.PaymentServiceV2.payV2(..)) || " +
                    "execution(* uz.repay.payment.service.business.PaymentServiceV2.payV3(..))) && args(payRequestDTO,..))",
            returning = "resultData",
            argNames = "payRequestDTO,resultData"
    )
    public void removeFromFraudTransactionInPay(PayRequestDTO payRequestDTO, ResultData<PayResponseDTO> resultData) {
        validateRequestAndResultData(payRequestDTO, resultData);
    }

    @AfterReturning(
            pointcut = "(execution(* uz.repay.payment.service.business.PaymentServiceV2.forcePay(..)) || " +
                    "execution(* uz.repay.payment.service.business.PaymentServiceV2.forcePayV3(..))) && args(payRequestDTO,..))",
            returning = "resultData",
            argNames = "payRequestDTO,resultData"
    )
    public void removeFromFraudTransactionInForcePay(PayRequestDTO payRequestDTO, ResultData<PaymentForcePayResponseDTO> resultData) {
        validateRequestAndResultData(payRequestDTO, resultData);
    }

    private void validateRequestAndResultData(PayRequestDTO payRequestDTO, ResultData<?> resultData) {
        log.info("Starting remove fraud from loan with request: {} and response: {}", payRequestDTO, resultData);

        if (resultData != null)
            if (resultData.getIsSuccess()) {
                log.info("Result is success and will be deleted from fraud");
                removeTransactionFromFraud(payRequestDTO);
            } else {

                Integer code = resultData.getError().getCode();
                log.info("Checking code: {} for fraud nature", code);

                if (code.equals(ResultState.LOAN_DEBT_CLOSED.getCode())) {
                    remove(payRequestDTO, ResultState.LOAN_DEBT_CLOSED);
                    return;
                }

                if (code.equals(ResultState.INSUFFICIENT_FUNDS.getCode())) {
                    remove(payRequestDTO, ResultState.INSUFFICIENT_FUNDS);
                    return;
                }

                checkToPCNotFraudErrorCodes(payRequestDTO, code);
            }
    }

    private void remove(PayRequestDTO payRequestDTO, ResultState resultState) {
        log.info("Fraud code is {} and will be deleted from fraud...", resultState);
        removeTransactionFromFraud(payRequestDTO);
    }

    private void checkToPCNotFraudErrorCodes(PayRequestDTO payRequestDTO, Integer code) {
        log.info("Begin checking fraud code from not fraud codes list");
        Processing processing = payRequestDTO.getDebitParams().getProcessing();

        var pcNotFraudCodes = ErrorBuilder.notFraudCodes()
                .get(processing);


        if (pcNotFraudCodes != null && pcNotFraudCodes.contains(code)) {
            log.info("Found not fraud code: {} for pc: {}", code, processing);
            removeTransactionFromFraud(payRequestDTO);
        } else {
            log.warn("code: {} is fraud! ", code);
        }
    }

    private void removeTransactionFromFraud(PayRequestDTO requestDTO) {
        log.info("Remove transaction from fraud by: {} {}", requestDTO.getLoanExtId(), requestDTO.getParams().getCardPan());

        eventPublisher.publishEvent(
                new FraudEventDTO(requestDTO.getLoanExtId(), requestDTO.getParams().getCardPan())
        );

    }
}
