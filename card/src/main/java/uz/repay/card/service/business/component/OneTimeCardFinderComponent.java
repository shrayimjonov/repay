package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.service.core.TemporaryLoanDAO;
import uz.repay.card.service.core.component.HumoCardAdderComponent;
import uz.repay.card.service.core.component.MasterCardAdderComponent;
import uz.repay.card.service.core.component.UzCardAdderComponent;
import uz.repay.card.service.web.LoanService;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class OneTimeCardFinderComponent {
    private final TemporaryLoanDAO temporaryLoanDAO;
    private final HumoCardAdderComponent humoCardAdderComponent;
    private final LoanDAO loanDAO;
    private final UzCardAdderComponent uzCardAdderComponent;
    private final MasterCardAdderComponent masterCardAdderComponent;
    private final LoanService loanService;

    @Async
//    @SchedulerLock(name = "oneTimeCardFinder", lockAtMostFor = "PT1H", lockAtLeastFor = "PT30M")
    public void cardFind() {
        log.info("Card finder starter !!!");
        var loanList = temporaryLoanDAO.findAll();
        log.info("Start card finder for loans with size: {}", loanList.size());

        AtomicInteger loanCount = new AtomicInteger();
        final int[] loanCardsCount = {0};
        loanList
                .forEach(
                        l -> {
                            log.info("Loan check started for loan: {}", l.getLoanExtId());
                            var loanOptional = loanDAO.findLoanOptionalByExtId(l.getLoanExtId());

                            if (loanOptional.isEmpty()) {
                                log.error("Loan: {} not found", l.getLoanExtId());
                                try {
                                    var loanCreateResponse = loanService.addLoan(l.getLoanExtId());
                                    if (!(loanCreateResponse.getData() != null && loanCreateResponse.getData().getResult() != null && loanCreateResponse.getData().getResult().getCode() == 0)) {
                                        return;
                                    }
                                    loanOptional = loanDAO.findLoanOptionalByExtId(l.getLoanExtId());
                                    if (loanOptional.isEmpty()) {
                                        return;
                                    }
                                } catch (Exception e) {
                                    log.error("Loan create not succeed with loanExtId: {}. Error: {}", l.getLoanExtId(), e.getMessage());
                                    return;
                                }
                            }

                            loanCount.getAndIncrement();
                            var loan = loanOptional.get();

                            try {
                                var huMoResult = humoCardAdderComponent.checkAndHandleCards(loan);

                                loanCardsCount[0] = loanCardsCount[0] + huMoResult.getResult().getCards().size();
                            } catch (Exception e) {
                                log.info("Humo card finder error: {}", e.getMessage());
                            }

                            try {
                                var uzCardResult = uzCardAdderComponent.getCardFinderResponse(loan);

                                loanCardsCount[0] = loanCardsCount[0] + uzCardResult.getResult().getCards().size();
                            } catch (Exception e) {
                                log.info("Uzcard card finder error: {}", e.getMessage());
                            }

                            try {
                                var masterResult = masterCardAdderComponent.getCardFinderResponse(loan);

                                loanCardsCount[0] = loanCardsCount[0] + masterResult.getResult().getCards().size();
                            } catch (Exception e) {
                                log.error("Mastercard card finder error: {}", e.getMessage());
                            }

                        }
                );

        log.info("Card finder method finished with Loans count: {} and cards count: {}", loanCount, loanCardsCount);

    }
}
