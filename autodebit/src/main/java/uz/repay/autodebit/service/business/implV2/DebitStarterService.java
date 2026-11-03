package uz.repay.autodebit.service.business.implV2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO;
import uz.repay.autodebit.model.core.response.AutoPayResultV2;
import uz.repay.autodebit.model.payment.response.PayResponseDTO;
import uz.repay.autodebit.service.core.LoanResultService;
import uz.repay.autodebit.service.web.CardFeignService;
import uz.repay.autodebit.service.web.LoanFeignService;
import uz.repay.autodebit.service.web.PaymentFeignService;
import uz.repay.autodebit.utils.AutopayUtils;
import uz.repay.autodebit.utils.ErrorUtils;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.request.payment.params.NotificationParams;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.core.result.LoanCardsResult;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardUtils;
import uz.repay.common.utils.DateUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class DebitStarterService implements Runnable {
    private String branch;
    private Long minimumDebitAmount;
    private volatile boolean isActive;
    private List<String> debtorsList;
    private CardFeignService cardFeignService;
    private LoanFeignService loanFeignService;
    private PaymentFeignService paymentFeignService;
    private UUID workerId;
    private volatile boolean isFinished = false;
    private LoanResultService loanResultService;
    private volatile boolean isStarted = false;
    private AutoPayResultV2 result;
    private UUID debitServiceId;
    private boolean isInsolvent;
    private BooleanSupplier activeStateSupplier = () -> true;

    public DebitStarterService(String branch, Long minimumDebitAmount, boolean isActive,
                               List<String> debtorsList, CardFeignService cardFeignService,
                               LoanFeignService loanFeignService, PaymentFeignService paymentFeignService,
                               UUID workerId, boolean isFinished, LoanResultService loanResultService,
                               boolean isStarted, AutoPayResultV2 result, UUID debitServiceId,
                               boolean isInsolvent) {
        this(branch, minimumDebitAmount, isActive, debtorsList, cardFeignService, loanFeignService,
                paymentFeignService, workerId, isFinished, loanResultService, isStarted, result, debitServiceId,
                isInsolvent, () -> true);
    }

    public DebitStarterService(String branch, Long minimumDebitAmount, boolean isActive,
                               List<String> debtorsList, CardFeignService cardFeignService,
                               LoanFeignService loanFeignService, PaymentFeignService paymentFeignService,
                               UUID workerId, boolean isFinished, LoanResultService loanResultService,
                               boolean isStarted, AutoPayResultV2 result, UUID debitServiceId,
                               boolean isInsolvent, BooleanSupplier activeStateSupplier) {
        this.branch = branch;
        this.minimumDebitAmount = minimumDebitAmount;
        this.isActive = isActive;
        this.debtorsList = debtorsList;
        this.cardFeignService = cardFeignService;
        this.loanFeignService = loanFeignService;
        this.paymentFeignService = paymentFeignService;
        this.workerId = workerId;
        this.isFinished = isFinished;
        this.loanResultService = loanResultService;
        this.isStarted = isStarted;
        this.result = result;
        this.debitServiceId = debitServiceId;
        this.isInsolvent = isInsolvent;
        this.activeStateSupplier = activeStateSupplier != null ? activeStateSupplier : () -> true;
    }

    @Override
    public void run() {
        var uniqueIDForThread = UUID.randomUUID();
        var startTime = DateUtils.getDateTimeNow();
        var debtorsSize = debtorsList != null ? debtorsList.size() : 0;
        log.info("Begin worker with session: {} for branch: {} with debtors size: {} started at: {}",
                uniqueIDForThread, branch, debtorsSize, startTime);

        if (debtorsList == null || debtorsList.isEmpty()) {
            log.info("Debtors list empty and starter {} is finished", Thread.currentThread().getName());
            return;
        }

        var resolvedMinimumDebitAmount = minimumDebitAmount != null ? minimumDebitAmount : 0L;

        for (var loan : debtorsList) {
            if (isAutoPaymentInActive() || Boolean.TRUE.equals(result.getEmergencyExit())) {
                break;
            }

            var loanKey = loan != null && !loan.isBlank() ? loan : "UNKNOWN_LOAN";
            if (loan == null || loan.isBlank()) {
                handleError(result, loanKey, ResultState.WrongParams, "Loan ext id is empty");
                continue;
            }

            log.debug("Begin auto payment for: {}", loan);

            try {
                var loanCardsResponse = cardFeignService.getLoansCardsV2(new LoanCardsRequestDTO(List.of(loan)));

                if (loanCardsResponse == null || loanCardsResponse.getError() != null || loanCardsResponse.getResult() == null) {
                    log.error("Loan card response {} and will be returned", loanCardsResponse);
                    handleError(result, loan, ResultState.WrongParams,
                            loanCardsResponse != null && loanCardsResponse.getError() != null
                                    ? loanCardsResponse.getError().getMessage()
                                    : "Loan cards response is empty");
                    continue;
                }

                var loanCardsOptional = loanCardsResponse.getResult().stream()
                        .filter(response -> response != null && loan.equals(response.getLoanExtId()))
                        .findFirst();

                if (loanCardsOptional.isEmpty() || loanCardsOptional.get().getCards() == null || loanCardsOptional.get().getCards().isEmpty()) {
                    log.error("Loan: {} cards is empty", loan);
                    handleError(result, loan, ResultState.WrongParams, "Loan cards is empty");
                    continue;
                }

                var loanCards = loanCardsOptional.get();
                log.debug("Loan cards result. loan: {} cards: {}", loan, loanCards.getCards());
                loanCards.setCards(new ArrayList<>(loanCards.getCards()));
                loanCards.getCards().sort(Comparator.comparing(
                        card -> card != null && card.getPayer() != null ? card.getPayer().getPriority() : Integer.MAX_VALUE
                ));

                var rate = visaCardsSeparateAndSetBalance(loanCards);
                var totalAmountInCards = calculateTotalAmount(loanCards);
                var loanResponse = loanFeignService.getLoanAdditionalInfo(loan);

                if (loanResponse == null || loanResponse.getError() != null || loanResponse.getResult() == null) {
                    log.error("Loan response has error: {}",
                            loanResponse != null && loanResponse.getError() != null
                                    ? loanResponse.getError().getMessage()
                                    : "Loan response is empty");
                    handleError(result, loan, ResultState.WrongParams,
                            loanResponse != null && loanResponse.getError() != null
                                    ? loanResponse.getError().getMessage()
                                    : "Loan response is empty");
                    continue;
                }

                var loanResult = loanResponse.getResult();
                Long recommendedAmount = loanResult.getRecommendedAmount();

                if (recommendedAmount == null) {
                    handleError(result, loan, ResultState.WrongParams, "Loan recommended amount is empty");
                    continue;
                }

                if (!isInsolvent) {
                    Long transitBalance = loanResult.getTransitBalance() != null ? loanResult.getTransitBalance() : 0L;
                    if (transitBalance > 0) {
                        log.info("Transit balance has amount: {} and will be minus from recommended amount", transitBalance);
                        recommendedAmount -= transitBalance;
                    }

                    if (recommendedAmount <= 0) {
                        log.warn("Payment exited cause loan recommended amount is: {}", recommendedAmount);
                        handleError(result, loan, ResultState.NotProcessed, "Recommended amount is 0");
                        removeDebtLoanWithoutDebt(loan);
                        continue;
                    }

                    if (totalAmountInCards < resolvedMinimumDebitAmount && recommendedAmount > resolvedMinimumDebitAmount) {
                        var cardsBalanceToLog = loanCards.getCards().stream()
                                .filter(Objects::nonNull)
                                .map(card -> "[%s %d]".formatted(card.getPan(), getPositiveAmount(card.getAmount())))
                                .reduce("", (a, b) -> a + " " + b);

                        var comment = "Debit amount: %s less than minimum amount: %s. Details: %s"
                                .formatted(totalAmountInCards, resolvedMinimumDebitAmount, cardsBalanceToLog);
                        log.warn(comment);
                        handleError(result, loan, ResultState.NotProcessed, comment);
                        continue;
                    }
                } else {
                    recommendedAmount = totalAmountInCards;
                    if (recommendedAmount == 0) {
                        handleError(result, loan, ResultState.NotProcessed, "All cards have zero balance. Insolvent auto payment finished!");
                        continue;
                    }
                }

                if (loopCardsToDebit(loanResult, recommendedAmount, loanCards, result, rate, resolvedMinimumDebitAmount)) {
                    result.setEmergencyExit(true);
                    break;
                }

                log.debug("Loan: {} payment process finished", loan);
            } catch (Exception error) {
                log.error("Unexpected error while processing loan {}", loanKey, error);
                handleError(result, loanKey, ResultState.InternalServerError,
                        error.getMessage() != null ? error.getMessage() : "Unexpected error");
            }
        }

        log.info("End worker with session: {} for branch: {} and finished at: {}",
                uniqueIDForThread, branch, DateUtils.getDateTimeNow());
    }

    private Long visaCardsSeparateAndSetBalance(LoansCardsResponseDTO<LoanCardsResult> loanCards) {
        if (loanCards.getCards().stream().noneMatch(card -> card != null && card.getProcessing() == Processing.DAVR_VISA)) {
            return 0L;
        }

        var response = handleVisaBalance(loanCards);
        if (!Boolean.TRUE.equals(response.getStatus())) {
            return -1L;
        }

        Long rate = response.getData();
        log.debug("Taken rate: {}", rate);

        loanCards.getCards().stream()
                .filter(card -> card != null && card.getProcessing() == Processing.DAVR_VISA)
                .forEach(card -> card.setAmount(rate * getPositiveAmount(card.getAmount()) / 100));

        return rate;
    }

    private SimpleResponse<Long> handleVisaBalance(LoansCardsResponseDTO<LoanCardsResult> loanCards) {
        try {
            var bankCourseResponse = paymentFeignService.getCourse();

            if (bankCourseResponse == null ||
                    !Boolean.TRUE.equals(bankCourseResponse.getIsSuccess()) ||
                    bankCourseResponse.getResult() == null ||
                    bankCourseResponse.getResult().stream().noneMatch(rate -> rate != null && "USD".equals(rate.getAbbr()))) {
                loanCards.getCards().removeIf(card -> card != null && card.getProcessing() == Processing.DAVR_VISA);
                return visaRateFailureResponse(-1, "Course response not fetched");
            }

            var rateOptional = bankCourseResponse.getResult().stream()
                    .filter(rate -> rate != null && "USD".equals(rate.getAbbr()))
                    .findFirst();

            if (rateOptional.isEmpty() || rateOptional.get().getPurchase() == null || rateOptional.get().getPurchase() == 0) {
                log.error("Purchase rate is null");
                loanCards.getCards().removeIf(card -> card != null && card.getProcessing() == Processing.DAVR_VISA);
                return visaRateFailureResponse(-2, "Rate is null");
            }

            return new SimpleResponse<>(rateOptional.get().getPurchase());
        } catch (Exception error) {
            log.error("Error taking visa rate: {}", error.getMessage());
            loanCards.getCards().removeIf(card -> card != null && card.getProcessing() == Processing.DAVR_VISA);
            return visaRateFailureResponse(-1, "Course response not fetched");
        }
    }

    private void removeDebtLoanWithoutDebt(String loanExtId) {
        log.info("Loan: {} recommend is 0 and will be removed from debtLoan", loanExtId);
        try {
            var response = loanFeignService.removeDebtLoanByLoanExtId(loanExtId);
            if (response == null || !Boolean.TRUE.equals(response.getStatus()) ||
                    response.getData() == null || response.getData().getResult() == null || !response.getData().getResult()) {
                log.warn("Failed to remove loan {} from debtLoan.", loanExtId);
            }
        } catch (Exception error) {
            log.error("Error removing loan {} from debtLoan: {}", loanExtId, error.getMessage());
        }
    }

    private void handleError(AutoPayResultV2 result, String loanExtId,
                             ResultState resultState,
                             String errorMessage) {
        result.getFailedList().put(loanExtId, new ResultData<>(resultState, errorMessage));

        try {
            loanResultService.save(loanExtId, errorMessage, result.getSessionID(), workerId);
        } catch (Exception error) {
            log.error("Error saving loan result: {}", error.getMessage());
        }
    }

    private static long calculateTotalAmount(LoansCardsResponseDTO<LoanCardsResult> loanCards) {
        var totalAmount = loanCards.getCards().stream()
                .mapToLong(card -> card != null ? getPositiveAmount(card.getAmount()) : 0L)
                .sum();
        log.debug("In cards: {} total sum: {}", loanCards.getCards().size(), totalAmount);
        return totalAmount;
    }

    private boolean loopCardsToDebit(LoanAdditionalResponseDTO loanInfo, Long amount,
                                     LoansCardsResponseDTO<LoanCardsResult> loanCards, AutoPayResultV2 result,
                                     Long rate, Long minimumDebitAmount) {
        var loanExtId = loanInfo.getLoanExtId();
        AtomicReference<Long> recommendedAmount = new AtomicReference<>(amount);
        String skipReason = null;

        for (var card : loanCards.getCards()) {
            if (isAutoPaymentInActive() || Boolean.TRUE.equals(result.getEmergencyExit())) {
                return false;
            }

            log.debug("Recommended amount: {}", recommendedAmount.get());

            if (recommendedAmount.get() <= 0) {
                break;
            }

            if (card == null) {
                skipReason = "No debitable cards found";
                continue;
            }

            if (card.getDebitParams() == null) {
                log.warn("Card {} skipped because debit params are missing", card.getPan());
                skipReason = "All cards were skipped because debit params are missing";
                continue;
            }

            var isCardConfirmed = isConfirmedSvgateAutopayCard(card);
            var minimumBalanceToKeep = resolveMinimumBalanceToKeep(
                    card.getProcessing(),
                    isCardConfirmed,
                    AutopayUtils.minimumDebitAmountForMinimumReminder
            );

            if (!hasEnoughBalanceForDebit(
                    card.getAmount(),
                    recommendedAmount.get(),
                    minimumBalanceToKeep,
                    card.getProcessing()
            )) {
                log.warn("Card balance: {} is not enough for minimum remainder after debit for processing: {}. Min amount: {}. Recommended amount: {}",
                        card.getAmount(), card.getProcessing(), minimumBalanceToKeep, recommendedAmount.get());
                skipReason = "All cards were skipped because the minimum remainder rule would be violated";
                continue;
            }

            Long availableAmountForDebit = calculateAvailableAmountForDebit(card.getAmount(), minimumBalanceToKeep);
            Long paidAmount = calculatePaidAmount(availableAmountForDebit, recommendedAmount.get(), minimumDebitAmount);
            if (paidAmount == null) {
                log.error("Card: {} amount: {} and not will be debited", card.getPan(), card.getAmount());
                handleError(result, loanExtId, ResultState.NotProcessed,
                        "Card: %s amount: %s and not will be debited".formatted(card.getPan(), card.getAmount()));
                continue;
            }

            if (!CardUtils.isSumPC(card.getProcessing()) && (rate == null || rate == 0)) {
                String message = "Rate is null for debiting from Card: %s".formatted(card.getPan());
                log.error(message);
                handleError(result, loanExtId, ResultState.NotProcessed, message);
                continue;
            }

            var originalSumAmount = paidAmount;
            if (!CardUtils.isSumPC(card.getProcessing())) {
                Double newAmount = paidAmount.doubleValue() / rate.doubleValue() * 100;
                paidAmount = newAmount.longValue();
            }

            log.debug("Paid amount: {} from card: {}", paidAmount, card.getPan());

            ResultData<PayResponseDTO> payResponse;
            if (Processing.SVGATE_AUTOPAY == card.getProcessing()) {
                Long payAmount = loanInfo.getRecommendedAmount();
                if (payAmount != null && payAmount > card.getAmount()) {
                    payAmount = card.getAmount();
                }
                var request = payRequestBuilder(loanInfo, card, payAmount);
                payResponse = paymentFeignService.payV3(request);
                if (shouldFallbackToPayV2(payResponse)) {
                    var secondRequest = payRequestBuilder(loanInfo, card, paidAmount);
                    payResponse = paymentFeignService.payV2(secondRequest);
                }
            } else {
                var request = payRequestBuilder(loanInfo, card, paidAmount);
                payResponse = paymentFeignService.payV2(request);
            }
            log.debug("Payment v2 response: {}", payResponse);

            if (payResponse == null || payResponse.getError() != null) {
                if (payResponse != null && payResponse.getError() != null) {
                    checkCodeToBlockCard(card, payResponse);
                    handleError(result, loanExtId, ResultState.NotProcessed,
                            payResponse.getError().getMessage() + " from card: %s ".formatted(card.getPan()));

                    if (ResultState.BlockOperation.getCode().equals(payResponse.getError().getCode())) {
                        log.info("Stop autoPayment cause payment is turned off");
                        return true;
                    }

                    if (isUnknownPaymentOutcome(payResponse)) {
                        // The debit may have executed. Do NOT try the next card -
                        // if the first debit actually landed, a second one overcharges.
                        result.getUnknownOutcomeList().put(loanExtId,
                                "Unknown payment outcome from card: %s".formatted(card.getPan()));
                        log.warn("Unknown payment outcome for loan: {} card: {} - stopping card loop", loanExtId, card.getPan());
                        break;
                    }
                } else {
                    handleError(result, loanExtId, ResultState.NotProcessed, "Pay response is empty");
                    result.getUnknownOutcomeList().put(loanExtId, "Pay response is empty");
                    break;
                }
                continue;
            }

            result.getSuccessList().put(loanExtId, payResponse);
            recommendedAmount.set(recommendedAmount.get() - originalSumAmount);
        }

        if (!result.getSuccessList().containsKey(loanExtId) && !result.getFailedList().containsKey(loanExtId)) {
            handleError(result, loanExtId, ResultState.NotProcessed,
                    skipReason != null ? skipReason : "No debitable cards found");
        }

        return false;
    }

    /**
     * The payment service gave no definitive answer (timeout / server error):
     * money may or may not have moved. Package-private for unit tests.
     */
    static boolean isUnknownPaymentOutcome(ResultData<PayResponseDTO> payResponse) {
        if (payResponse == null) {
            return true;
        }
        if (payResponse.getError() == null) {
            return false;
        }

        var errorCode = payResponse.getError().getCode();
        return ResultState.TimeOutError.getCode().equals(errorCode)
                || ResultState.InternalServerError.getCode().equals(errorCode);
    }

    /**
     * True only when payV3 definitively rejected the payment. Unknown outcomes
     * (timeout, 5xx, empty response) must NOT trigger a second debit attempt.
     * Package-private for unit tests.
     */
    static boolean shouldFallbackToPayV2(ResultData<PayResponseDTO> payResponse) {
        if (payResponse == null) {
            return false;
        }

        if (payResponse.getError() == null) {
            return !Boolean.TRUE.equals(payResponse.getIsSuccess());
        }

        var errorCode = payResponse.getError().getCode();
        return !ResultState.TimeOutError.getCode().equals(errorCode)
                && !ResultState.ThirdPartyError.getCode().equals(errorCode)
                && !ResultState.InternalServerError.getCode().equals(errorCode);
    }

    private void checkCodeToBlockCard(LoanCardsResult card, ResultData<PayResponseDTO> payResponse) {
        var signalCode = ErrorUtils.statusIsCardBlocked(card.getProcessing(), payResponse.getError().getCode());

        if (signalCode && card.getId() != null) {
            var response = cardFeignService.deactivateCard(card.getId());
            log.info("Deactivate card response: {}", response);
        }
    }

    private boolean hasEnoughBalanceForDebit(Long cardBalance, Long recommendedAmount, Long minimumBalanceToKeep,
                                             Processing processing) {
        log.debug("Check card balance: {} for processing: {}. Min amount: {}. Recommended amount: {}",
                cardBalance, processing, minimumBalanceToKeep, recommendedAmount);

        if (minimumBalanceToKeep <= 0) {
            return true;
        }

        if (cardBalance == null || cardBalance.compareTo(minimumBalanceToKeep) <= 0) {
            log.debug("Card balance: {} is less than or equal minimum debit amount: {} for processing: {}",
                    cardBalance, minimumBalanceToKeep, processing);
            return false;
        }

        return true;
    }


    private Long calculateAvailableAmountForDebit(Long cardBalance, Long minimumBalanceToKeep) {
        return Math.max(0L, getPositiveAmount(cardBalance) - (minimumBalanceToKeep != null ? minimumBalanceToKeep : 0L));
    }

    private Long calculatePaidAmount(Long availableAmountForDebit, Long recommendedAmount, Long minimumDebitAmount) {
        if (availableAmountForDebit == null || recommendedAmount == null || availableAmountForDebit <= 0) {
            return null;
        }

        if (availableAmountForDebit >= recommendedAmount) {
            return recommendedAmount;
        }

        if (availableAmountForDebit > minimumDebitAmount) {
            return availableAmountForDebit;
        }

        return null;
    }

    private SimpleResponse<Long> visaRateFailureResponse(Integer code, String message) {
        return new SimpleResponse<>(code, message);
    }

    private long resolveMinimumBalanceToKeep(Processing processing, boolean isCardConfirmed, Long minimumBalanceToKeep) {
        if (!requiresMinimumRemainder(processing)) {
            return 0L;
        }

        if (Processing.SVGATE_AUTOPAY == processing && isCardConfirmed) {
            return 0L;
        }

        return minimumBalanceToKeep != null ? Math.max(0L, minimumBalanceToKeep) : 0L;
    }

    private boolean requiresMinimumRemainder(Processing processing) {
        return AutopayUtils.supportedProcessingTypesForMinimumReminder.contains(processing);
    }

    private boolean isConfirmedSvgateAutopayCard(LoanCardsResult card) {
        if (card == null || Processing.SVGATE_AUTOPAY != card.getProcessing() || card.getPan() == null || card.getPan().isBlank()) {
            return false;
        }

        var cardInfo = cardFeignService.getCardByPan(new CardFullInfoRequestDTO(card.getPan()));
        return cardInfo != null
                && cardInfo.getData() != null
                && cardInfo.getData().getResult() != null
                && Boolean.TRUE.equals(cardInfo.getData().getResult().getIsConfirmed());
    }

    private static long getPositiveAmount(Long amount) {
        return amount != null && amount > 0 ? amount : 0L;
    }

    private boolean isAutoPaymentInActive() {
        return !isActive || !activeStateSupplier.getAsBoolean();
    }

    private static PayRequestDTO payRequestBuilder(LoanAdditionalResponseDTO loanInfo, LoanCardsResult card, Long paidAmount) {
        var loanExtId = loanInfo.getLoanExtId();
        var loanDebtor = loanInfo.getDebtor();
        var loanId = loanInfo.getId();

        var request = new PayRequestDTO(
                loanExtId,
                paidAmount,
                card.getDebitParams(),
                new PayRequestDTO.PayParams(
                        card.getPan(),
                        card.getHolder(),
                        card.getPayer()
                ),
                AddType.AUTO,
                new NotificationParams(
                        card.getPhone(),
                        loanDebtor,
                        loanId != null ? loanId : -1L,
                        loanInfo.getLanguage()
                )
        );

        log.debug("""
                        {
                            "loanExtId" : "{}",
                            "amount" : {},
                            "debitParams" : {
                                "type" : "{}",
                                "card" : "{}",
                                "processing" : "{}"
                            },
                            "params" : {
                                "cardPan" : "{}",
                                "cardOwner" : "{}"
                            },
                            "addType" : "AUTO",
                            "notificationParams" : {
                                "phone" : "{}",
                                "fullName" : "{}",
                                "loanId" : {}
                            }
                        }
                        """,
                loanExtId,
                paidAmount,
                card.getDebitParams().getType(),
                card.getDebitParams().getCard(),
                card.getDebitParams().getProcessing(),
                card.getPan(),
                card.getHolder(),
                card.getPhone(),
                loanDebtor,
                loanId
        );

        return request;
    }
}
