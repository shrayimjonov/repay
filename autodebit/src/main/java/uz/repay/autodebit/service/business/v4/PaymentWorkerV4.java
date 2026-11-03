package uz.repay.autodebit.service.business.v4;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uz.repay.autodebit.client.webClient.CardWebClient;
import uz.repay.autodebit.client.webClient.LoanWebClient;
import uz.repay.autodebit.client.webClient.PaymentWebClient;
import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO;
import uz.repay.autodebit.model.core.response.AutoPayResultV2;
import uz.repay.autodebit.model.payment.response.PayResponseDTO;
import uz.repay.autodebit.model.property.WorkerProperty;
import uz.repay.autodebit.service.core.LoanResultService;
import uz.repay.autodebit.utils.AutopayUtils;
import uz.repay.autodebit.utils.ErrorUtils;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Getter
@Setter
public class PaymentWorkerV4 {
    @NonNull
    private final String branch;
    private final WorkerProperty workerProperty;
    private final boolean isActive;
    private final List<String> debtorsList;
    private final CardWebClient cardWebClient;
    private final LoanWebClient loanWebClient;
    private final PaymentWebClient paymentWebClient;
    private final UUID workerId;
    private final LoanResultService loanResultService;
    private final AutoPayResultV2 result;
    private final boolean isInsolvent;

    public PaymentWorkerV4(@NotNull String branch, WorkerProperty workerProperty, boolean isActive,
                           List<String> debtorsList, CardWebClient cardWebClient, LoanWebClient loanWebClient,
                           PaymentWebClient paymentWebClient, UUID workerId, LoanResultService loanResultService,
                           boolean isInsolvent) {
        this.branch = branch;
        this.workerProperty = workerProperty;
        this.isActive = isActive;
        this.debtorsList = debtorsList;
        this.cardWebClient = cardWebClient;
        this.loanWebClient = loanWebClient;
        this.paymentWebClient = paymentWebClient;
        this.workerId = workerId;
        this.loanResultService = loanResultService;
        this.isInsolvent = isInsolvent;
        this.result = new AutoPayResultV2(branch);
        this.result.setSuccessList(new ConcurrentHashMap<>());
        this.result.setFailedList(new ConcurrentHashMap<>());
    }

    public Mono<AutoPayResultV2> callReactive() {
        log.info("Starting payment process for branch: {}", branch);

        if (!isActive || debtorsList == null || debtorsList.isEmpty()) {
            log.info("Debtors list empty or worker inactive for branch: {}", branch);
            return Mono.just(result);
        }

        return Flux.fromIterable(debtorsList)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(debtor -> processLoan(debtor)
                        .doOnError(error -> log.error("Error processing loan {}: {}", debtor, error.getMessage()))
                        .onErrorResume(error -> Mono.empty()))
                .sequential()
                .doOnComplete(() -> log.info("Finished processing all loans for branch: {}", branch))
                .onErrorResume(throwable -> {
                    log.error("Unexpected error in payment process: {}", throwable.getMessage());
                    return Mono.empty();
                })
                .then(Mono.just(result));
    }

    private Mono<Void> processLoan(String loanExtId) {
        if (!isActive || Boolean.TRUE.equals(result.getEmergencyExit())) {
            return Mono.empty();
        }

        var loanKey = loanExtId != null && !loanExtId.isBlank() ? loanExtId : "UNKNOWN_LOAN";
        if (loanExtId == null || loanExtId.isBlank()) {
            handleError(result, loanKey, ResultState.WrongParams, "Loan ext id is empty");
            return Mono.empty();
        }

        return cardWebClient.getLoansCardsV2(new LoanCardsRequestDTO(List.of(loanExtId)))
                .flatMap(loanCardsResponse -> {
                    if (loanCardsResponse == null || loanCardsResponse.getError() != null || loanCardsResponse.getResult() == null) {
                        log.error("Loan card response {} and will be returned", loanCardsResponse);
                        handleError(result, loanExtId, ResultState.WrongParams,
                                loanCardsResponse != null && loanCardsResponse.getError() != null
                                        ? loanCardsResponse.getError().getMessage()
                                        : "Loan cards response is empty");
                        return Mono.empty();
                    }

                    var loanCardsOptional = loanCardsResponse.getResult().stream()
                            .filter(z -> z != null && loanExtId.equals(z.getLoanExtId()))
                            .findFirst();

                    if (loanCardsOptional.isEmpty() || loanCardsOptional.get().getCards() == null || loanCardsOptional.get().getCards().isEmpty()) {
                        log.error("Loan: {} cards is empty", loanExtId);
                        handleError(result, loanExtId, ResultState.WrongParams, "Loan cards is empty");
                        return Mono.empty();
                    }

                    var loanCards = loanCardsOptional.get();
                    loanCards.setCards(new ArrayList<>(loanCards.getCards()));

                    return visaCardsSeparateAndSetBalance(loanCards)
                            .flatMap(rate -> loanWebClient.getLoanAdditionalInfo(new LoanSimpleRequestDTO(loanExtId))
                                    .flatMap(loanResponse -> {
                                        if (loanResponse == null || loanResponse.getError() != null || loanResponse.getResult() == null) {
                                            log.error("Loan response has error: {}",
                                                    loanResponse != null && loanResponse.getError() != null
                                                            ? loanResponse.getError().getMessage()
                                                            : "Loan response is empty");
                                            handleError(result, loanExtId, ResultState.WrongParams,
                                                    loanResponse != null && loanResponse.getError() != null
                                                            ? loanResponse.getError().getMessage()
                                                            : "Loan response is empty");
                                            return Mono.empty();
                                        }

                                        var minimumDebitAmount = workerProperty != null && workerProperty.getMinimumDebitAmount() != null
                                                ? workerProperty.getMinimumDebitAmount()
                                                : 0L;
                                        var loanResult = loanResponse.getResult();
                                        Long recommendedAmount = loanResult.getRecommendedAmount();

                                        if (recommendedAmount == null) {
                                            handleError(result, loanExtId, ResultState.WrongParams, "Loan recommended amount is empty");
                                            return Mono.empty();
                                        }

                                        if (!isInsolvent) {
                                            Long transitBalance = loanResult.getTransitBalance() != null ? loanResult.getTransitBalance() : 0L;
                                            if (transitBalance > 0) {
                                                log.info("Transit balance detected: {}. Deducting from recommended amount.", transitBalance);
                                                recommendedAmount -= transitBalance;
                                            }

                                            if (recommendedAmount <= 0) {
                                                log.warn("Skipping loan {}: recommended amount is {}", loanExtId, recommendedAmount);
                                                handleError(result, loanExtId, ResultState.NotProcessed, "Recommended amount is 0");
                                                removeDebtLoanWithoutDebt(loanExtId);
                                                return Mono.empty();
                                            }

                                            Long totalAmountInCards = calculateTotalAmount(loanCards);
                                            if (totalAmountInCards < minimumDebitAmount && recommendedAmount > minimumDebitAmount) {
                                                var cardsBalanceToLog = loanCards.getCards().stream()
                                                        .filter(card -> card != null)
                                                        .map(card -> "[%s %d]".formatted(card.getPan(), getPositiveAmount(card.getAmount())))
                                                        .reduce("", (a, b) -> a + " " + b);

                                                var comment = "Debit amount: %s less than minimum amount: %s. Details: %s"
                                                        .formatted(totalAmountInCards, minimumDebitAmount, cardsBalanceToLog);
                                                log.warn(comment);
                                                handleError(result, loanExtId, ResultState.NotProcessed, comment);
                                                return Mono.empty();
                                            }
                                        } else {
                                            recommendedAmount = calculateTotalAmount(loanCards);
                                            if (recommendedAmount == 0) {
                                                handleError(result, loanExtId, ResultState.NotProcessed, "All cards have zero balance. Insolvent auto payment finished!");
                                                return Mono.empty();
                                            }
                                        }

                                        return loopCardsToDebit(loanResult, recommendedAmount, loanCards, result, rate, minimumDebitAmount).then();
                                    }));
                })
                .onErrorResume(error -> {
                    log.error("Unexpected error while processing loan {}", loanExtId, error);
                    handleError(result, loanExtId, ResultState.InternalServerError,
                            error.getMessage() != null ? error.getMessage() : "Unexpected error");
                    return Mono.empty();
                })
                .then();
    }

    private void removeDebtLoanWithoutDebt(String loanExtId) {
        log.info("Loan: {} recommend is 0 and will be removed from debtLoan", loanExtId);

        loanWebClient.removeDebtLoanByLoanExtId(loanExtId)
                .flatMap(response -> {
                    if (response == null || response.getResult() == null || !response.getResult()) {
                        log.warn("Failed to remove loan {} from debtLoan.", loanExtId);
                    }
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.error("Error removing loan {} from debtLoan: {}", loanExtId, e.getMessage());
                    return Mono.empty();
                })
                .subscribe();
    }

    private static long calculateTotalAmount(LoansCardsResponseDTO<LoanCardsResult> loanCards) {
        var totalAmount = loanCards.getCards().stream()
                .mapToLong(card -> card != null ? getPositiveAmount(card.getAmount()) : 0L)
                .sum();
        log.info("In cards: {} total sum: {}", loanCards.getCards().size(), totalAmount);
        return totalAmount;
    }

    private Mono<Long> visaCardsSeparateAndSetBalance(LoansCardsResponseDTO<LoanCardsResult> loanCards) {
        if (loanCards.getCards().stream().noneMatch(card -> card != null && card.getProcessing() == Processing.DAVR_VISA)) {
            return Mono.just(0L);
        }

        return handleVisaBalance(loanCards)
                .flatMap(response -> {
                    if (!Boolean.TRUE.equals(response.getStatus())) {
                        return Mono.just(-1L);
                    }

                    Long rate = response.getData();
                    log.info("Visa exchange rate: {}", rate);

                    loanCards.getCards().stream()
                            .filter(card -> card != null && card.getProcessing() == Processing.DAVR_VISA)
                            .forEach(card -> card.setAmount(rate * getPositiveAmount(card.getAmount()) / 100));

                    return Mono.just(rate);
                });
    }

    private Mono<SimpleResponse<Long>> handleVisaBalance(LoansCardsResponseDTO<LoanCardsResult> loanCards) {
        return paymentWebClient.getCourse()
                .<SimpleResponse<Long>>map(bankCourseResponse -> {
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

                    return new SimpleResponse<Long>(rateOptional.get().getPurchase());
                })
                .onErrorResume(error -> {
                    log.error("Error taking visa rate: {}", error.getMessage());
                    loanCards.getCards().removeIf(card -> card != null && card.getProcessing() == Processing.DAVR_VISA);
                    return Mono.just(visaRateFailureResponse(-1, "Course response not fetched"));
                });
    }

    public Mono<Boolean> loopCardsToDebit(LoanAdditionalResponseDTO loanInfo,
                                          Long amount,
                                          LoansCardsResponseDTO<LoanCardsResult> loanCards,
                                          AutoPayResultV2 result,
                                          Long rate,
                                          Long minimumDebitAmount) {
        String loanExtId = loanInfo.getLoanExtId();
        AtomicReference<Long> recommendedAmount = new AtomicReference<>(amount);
        AtomicReference<String> skipReason = new AtomicReference<>();

        return Flux.fromIterable(loanCards.getCards())
                .concatMap(card -> processCardPayment(card, recommendedAmount, loanExtId, loanInfo, result, rate, minimumDebitAmount, skipReason))
                .then(Mono.fromRunnable(() -> {
                    if (!result.getSuccessList().containsKey(loanExtId) && !result.getFailedList().containsKey(loanExtId)) {
                        handleError(result, loanExtId, ResultState.NotProcessed,
                                skipReason.get() != null ? skipReason.get() : "No debitable cards found");
                    }
                }))
                .then(Mono.just(true));
    }

    private Mono<Void> processCardPayment(LoanCardsResult card,
                                          AtomicReference<Long> recommendedAmount,
                                          String loanExtId,
                                          LoanAdditionalResponseDTO loanInfo,
                                          AutoPayResultV2 result,
                                          Long rate,
                                          Long minimumDebitAmount,
                                          AtomicReference<String> skipReason) {
        if (!isActive || Boolean.TRUE.equals(result.getEmergencyExit()) || recommendedAmount.get() <= 0) {
            return Mono.empty();
        }

        if (card == null) {
            skipReason.set("No debitable cards found");
            return Mono.empty();
        }

        if (card.getDebitParams() == null) {
            log.warn("Card {} skipped because debit params are missing", card.getPan());
            skipReason.set("All cards were skipped because debit params are missing");
            return Mono.empty();
        }

        if (!checkCardBalanceForMinimumLimitAfterDebit(
                card.getAmount(),
                recommendedAmount.get(),
                AutopayUtils.minimumDebitAmountForMinimumReminder,
                card.getProcessing()
        )) {
            log.warn("Card balance: {} is not enough for minimum remainder after debit for processing: {}. Min amount: {}. Recommended amount: {}",
                    card.getAmount(), card.getProcessing(), AutopayUtils.minimumDebitAmountForMinimumReminder, recommendedAmount.get());
            skipReason.set("All cards were skipped because the minimum remainder rule would be violated");
            return Mono.empty();
        }

        Long availableAmountForDebit = calculateAvailableAmountForDebit(card.getAmount(), card.getProcessing());
        Long paidAmount = calculatePaidAmount(availableAmountForDebit, recommendedAmount.get(), minimumDebitAmount);
        if (paidAmount == null) {
            log.error("Card: {} amount: {} and not will be debited", card.getPan(), card.getAmount());
            handleError(result, loanExtId, ResultState.NotProcessed, "Card: %s amount: %s and not will be debited".formatted(card.getPan(), card.getAmount()));
            return Mono.empty();
        }

        if (!CardUtils.isSumPC(card.getProcessing()) && (rate == null || rate == 0)) {
            String message = "Rate is null for debiting from Card: %s".formatted(card.getPan());
            log.error(message);
            handleError(result, loanExtId, ResultState.NotProcessed, message);
            return Mono.empty();
        }

        var originalSumAmount = paidAmount;
        if (!CardUtils.isSumPC(card.getProcessing())) {
            Double newAmount = paidAmount.doubleValue() / rate.doubleValue() * 100;
            paidAmount = newAmount.longValue();
        }

        log.info("Paid amount: {} from card: {}", paidAmount, card.getPan());

        var request = payRequestBuilder(loanInfo, card, paidAmount);

        return paymentWebClient.payV2(request)
                .flatMap(payResponse -> {
                    if (payResponse == null || payResponse.getError() != null) {
                        if (payResponse != null && payResponse.getError() != null) {
                            checkCodeToBlockCard(card, payResponse);
                            handleError(result, loanExtId, ResultState.NotProcessed,
                                    payResponse.getError().getMessage() + " from card: %s ".formatted(card.getPan()));

                            if (ResultState.BlockOperation.getCode().equals(payResponse.getError().getCode())) {
                                result.setEmergencyExit(true);
                            }
                        } else {
                            handleError(result, loanExtId, ResultState.NotProcessed, "Pay response is empty");
                        }
                        return Mono.empty();
                    }

                    result.getSuccessList().put(loanExtId, payResponse);
                    recommendedAmount.set(recommendedAmount.get() - originalSumAmount);
                    return Mono.empty();
                })
                .onErrorResume(error -> {
                    log.error("Error processing payment for loan {}: {}", loanExtId, error.getMessage());
                    handleError(result, loanExtId, ResultState.InternalServerError,
                            error.getMessage() != null ? error.getMessage() : "Unexpected error");
                    return Mono.empty();
                })
                .then();
    }

    private void checkCodeToBlockCard(LoanCardsResult card, ResultData<PayResponseDTO> payResponse) {
        var signalCode = ErrorUtils.statusIsCardBlocked(card.getProcessing(), payResponse.getError().getCode());

        if (signalCode && card.getId() != null) {
            cardWebClient.deactivateCard(card.getId())
                    .doOnSuccess(response -> log.info("Deactivate card response: {}", response))
                    .doOnError(error -> log.error("Error deactivating card {}: {}", card.getId(), error.getMessage()))
                    .subscribe();
        }
    }

    private void handleError(AutoPayResultV2 result, String loanExtId, ResultState resultState, String errorMessage) {
        result.getFailedList().put(loanExtId, new ResultData<>(resultState, errorMessage));

        Mono.fromRunnable(() -> loanResultService.save(loanExtId, errorMessage, result.getSessionID(), workerId))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("Error saving loan result: {}", error.getMessage()))
                .subscribe();
    }

    public boolean checkCardBalanceForMinimumLimitAfterDebit(Long cardBalance, Long recommendedAmount, Long minimumDebitAmount, Processing processing) {
        log.info("Check card balance: {} for processing: {}. Min amount: {}. Recommended amount: {}",
                cardBalance, processing, minimumDebitAmount, recommendedAmount);

        if (!checkProcessing(processing)) {
            return true;
        }

        if (cardBalance == null || cardBalance.compareTo(minimumDebitAmount) <= 0) {
            log.info("Card balance: {} is less than or equal minimum debit amount: {} for processing: {}",
                    cardBalance, minimumDebitAmount, processing);
            return false;
        }

        return true;
    }

    public boolean checkProcessing(Processing processing) {
        return AutopayUtils.supportedProcessingTypesForMinimumReminder.contains(processing);
    }

    private Long calculateAvailableAmountForDebit(Long cardBalance, Processing processing) {
        var safeBalance = getPositiveAmount(cardBalance);

        if (checkProcessing(processing)) {
            return Math.max(0L, safeBalance - AutopayUtils.minimumDebitAmountForMinimumReminder);
        }

        return safeBalance;
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

    private static long getPositiveAmount(Long amount) {
        return amount != null && amount > 0 ? amount : 0L;
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

        log.info("""
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
