package uz.repay.autodebit.service.business.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO;
import uz.repay.autodebit.model.core.response.AutoPayResult;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.autodebit.service.web.CardFeignService;
import uz.repay.autodebit.service.web.LoanFeignService;
import uz.repay.autodebit.service.web.PaymentFeignService;
import uz.repay.autodebit.utils.AutopayUtils;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.request.payment.params.NotificationParams;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.result.LoanCardsResult;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class PaymentWorker implements Callable<AutoPayResult> {
    private String branch;
    private Long minimumDebitAmount;
    private boolean isActive;
    private List<DebtLoansResult> debtorsList;
    private CardFeignService cardFeignService;
    private LoanFeignService loanFeignService;
    private PaymentFeignService paymentFeignService;

    public PaymentWorker(String branch,
                         Long minimumDebitAmount,
                         boolean isActive,
                         List<DebtLoansResult> debtorsList,
                         CardFeignService cardFeignService,
                         LoanFeignService loanFeignService,
                         PaymentFeignService paymentFeignService) {
        this.isActive = isActive;
        this.branch = branch;
        this.minimumDebitAmount = minimumDebitAmount;
        this.debtorsList = debtorsList;
        this.cardFeignService = cardFeignService;
        this.loanFeignService = loanFeignService;
        this.paymentFeignService = paymentFeignService;
    }


    @Override
    public AutoPayResult call() {
        log.info(" ----------------Starting autoPaymentV1! ---------------");
        var result = new AutoPayResult(branch);
        var resolvedMinimumDebitAmount = minimumDebitAmount != null ? minimumDebitAmount : 0L;
        log.info("Start {} session for: {}", result.getSessionID(), branch);

        if (debtorsList == null || debtorsList.isEmpty()) {
            log.info("Debtors list empty and Worker {} is finished", Thread.currentThread().getName());
            return result;
        }


        for (var loan : debtorsList) {
            if (!isActive)
                break;

            var loanExtId = loan != null ? loan.getLoanExtId() : null;
            var loanKey = loanExtId != null ? loanExtId : "UNKNOWN_LOAN";

            if (loanExtId == null || loanExtId.isBlank()) {
                result.getFailedList().put(loanKey, new ResultData<>(
                        ResultState.WrongParams,
                        "Loan ext id is empty"
                ));
                continue;
            }

            try {
                log.info("Begin auto payment for: {}", loanExtId);

                var loanCardsResponse = cardFeignService.getLoansCards(
                        new LoanCardsRequestDTO(
                                List.of(loanExtId)
                        )
                );

                var cards = extractCards(loanCardsResponse);

                if (loanCardsResponse == null || loanCardsResponse.getError() != null || cards.isEmpty()) {
                    log.error("Loan card response {} and will be returned", loanCardsResponse);
                    result.getFailedList().put(loanExtId, new ResultData<>(
                            ResultState.WrongParams,
                            loanCardsResponse != null && loanCardsResponse.getError() != null ? loanCardsResponse.getError().getMessage() : "Card list is empty"));
                    continue;
                }

                var totalAmountInCards = cards
                        .stream()
                        .mapToLong(card -> getPositiveAmount(card != null ? card.getAmount() : null))
                        .sum();

                log.info("Cards total amount: {}", totalAmountInCards);

                if (totalAmountInCards > resolvedMinimumDebitAmount) {
                    var loanResponse = loanFeignService.getLoanBalance(loanExtId);


                    if (loanResponse == null || loanResponse.getError() != null || loanResponse.getResult() == null) {
                        log.error("Loan response has error: {}", loanResponse != null && loanResponse.getError() != null ? loanResponse.getError().getMessage() : "Loan response is empty");
                        result.getFailedList().put(loanExtId, new ResultData<>(
                                ResultState.WrongParams,
                                loanResponse != null && loanResponse.getError() != null ? loanResponse.getError().getMessage() : "Loan response is empty"
                        ));
                        continue;
                    }

                    if (loanResponse.getResult().getRecommendedAmount() == null) {
                        log.warn("Payment exited because loan recommended amount is null");
                        result.getFailedList().put(loanExtId, new ResultData<>(
                                ResultState.WrongParams,
                                "Loan recommended amount is empty"
                        ));
                        continue;
                    }

                    if (loanResponse.getResult().getRecommendedAmount() <= 0) {
                        log.warn("Payment exited cause loan recommended amount is: {} ", loanResponse.getResult().getRecommendedAmount());
                        result.getFailedList().put(loanExtId, new ResultData<>(
                                ResultState.NotProcessed,
                                "Payment exited cause loan recommended amount is: 0"
                        ));
                        continue;
                    }

                    AtomicReference<Long> recommendedAmount = new AtomicReference<>(loanResponse.getResult().getRecommendedAmount());
                    String skipReason = null;


                    for (var card : cards) {
                        if (recommendedAmount.get() <= 0) {
                            break;
                        }

                        if (card == null) {
                            log.warn("Loan {} contains null card entry", loanExtId);
                            skipReason = "No debitable cards found";
                            continue;
                        }

                        if (card.getDebitParams() == null) {
                            log.warn("Card {} skipped because debit params are missing", card.getPan());
                            skipReason = "All cards were skipped because debit params are missing";
                            continue;
                        }

                        if (checkCardBalanceForMinimumLimitAfterDebit(card.getAmount(), recommendedAmount.get(), AutopayUtils.minimumDebitAmountForMinimumReminder, card.getProcessing())) {
                            log.info("Card balance: {} is enough for minimum reminder after debit for processing: {}. Min amount: {}. Recommended amount: {}",
                                    card.getAmount(), card.getProcessing(), AutopayUtils.minimumDebitAmountForMinimumReminder, recommendedAmount.get());
                        } else {
                            log.warn("Card balance: {} is not enough for minimum reminder after debit for processing: {}. Min amount: {}. Recommended amount: {}",
                                    card.getAmount(), card.getProcessing(), AutopayUtils.minimumDebitAmountForMinimumReminder, recommendedAmount.get());
                            skipReason = "All cards were skipped because the minimum remainder rule would be violated";
                            continue;
                        }

                        Long availableAmountForDebit = calculateAvailableAmountForDebit(
                                card.getAmount(),
                                card.getProcessing()
                        );

                        Long paidAmount = calculatePaidAmount(availableAmountForDebit, recommendedAmount.get(), resolvedMinimumDebitAmount);
                        if (paidAmount == null) {
                            log.error("Card: {} amount: {} and not will be debited", card.getPan(), card.getAmount());
                            result.getFailedList().put(loanExtId, new ResultData<>(
                                    ResultState.NotProcessed,
                                    "Card: %s amount: %s and not will be debited".formatted(card.getPan(), card.getAmount())));
                            continue;
                        }

                        log.info("Paid amount: {} from card: {}", paidAmount, card.getPan());

                        var payResponse = paymentFeignService.pay(new PayRequestDTO(
                                loanExtId,
                                paidAmount,
                                card.getDebitParams(),
                                new PayRequestDTO.PayParams(
                                        card.getPan(),
                                        card.getHolder(),
                                        card.getPayer()
                                ),
                                AddType.AUTO,
                                new NotificationParams()
                        ));

                        if (payResponse == null || payResponse.getError() != null) {
                            result.getFailedList().put(loanExtId, new ResultData<>(
                                    ResultState.NotProcessed,
                                    payResponse != null && payResponse.getError() != null ? payResponse.getError().getMessage() : "Pay response is empty"
                            ));
                            continue;
                        }

                        result.getSuccessList().put(loanExtId, payResponse);
                        recommendedAmount.set(recommendedAmount.get() - paidAmount);
                    }

                    if (!result.getSuccessList().containsKey(loanExtId) && !result.getFailedList().containsKey(loanExtId)) {
                        result.getFailedList().put(loanExtId, new ResultData<>(
                                ResultState.NotProcessed,
                                skipReason != null ? skipReason : "No debitable cards found"
                        ));
                    }
                } else {
                    log.warn("Debit amount: {} less then minimum amount: {}", totalAmountInCards, minimumDebitAmount);
                    result.getFailedList().put(loanExtId, new ResultData<>(
                            ResultState.NotProcessed,
                            "Debit amount: %s less then minimum amount: %s"
                                    .formatted(totalAmountInCards, resolvedMinimumDebitAmount)));
                }
            } catch (Exception e) {
                log.error("Unexpected error while processing loan {}", loanKey, e);
                result.getFailedList().put(loanKey, new ResultData<>(
                        ResultState.InternalServerError,
                        e.getMessage() != null ? e.getMessage() : "Unexpected error"
                ));
            }
        }
        log.info("Branch: {} session {} is finished", branch, result.getSessionID());

        return result;
    }

    public void stopWorker() {
        isActive = false;
    }

    public boolean checkCardBalanceForMinimumLimitAfterDebit(Long cardBalance, Long recommendedAmount, Long minimumDebitAmount, Processing processing) {

        log.info(" ========== Check card balance: {} for processing: {}. Min amount: {}. Recommended amount: {} ==========", cardBalance, processing, minimumDebitAmount, recommendedAmount);

        if (!checkProcessing(processing)) {
            return true;
        }

        if (cardBalance == null || cardBalance.compareTo(minimumDebitAmount) <= 0) {
            log.info(" ========== Card balance: {} is less than or equal minimum debit amount: {} for processing: {} ==========",
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

    private List<LoanCardsResult> extractCards(ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> loanCardsResponse) {
        if (loanCardsResponse == null || loanCardsResponse.getResult() == null || loanCardsResponse.getResult().isEmpty()) {
            return Collections.emptyList();
        }

        var first = loanCardsResponse.getResult().getFirst();
        if (first == null || first.getCards() == null) {
            return Collections.emptyList();
        }

        return first.getCards();
    }

    private long getPositiveAmount(Long amount) {
        return amount != null && amount > 0 ? amount : 0L;
    }
}
