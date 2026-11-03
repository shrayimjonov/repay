package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.service.core.component.HumoCardAdderComponent;
import uz.repay.card.service.core.component.MasterCardAdderComponent;
import uz.repay.card.service.core.component.UzCardAdderComponent;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class CardAutoAdderComponent {
    private final HumoCardAdderComponent humoCardAdderComponent;
    private final UzCardAdderComponent uzCardAdderComponent;
    private final MasterCardAdderComponent masterCardAdderComponent;
    private final LoanDAO loanDAO;

    @Async
//    @Scheduled(cron = "0 0 3 * * ?")
    @SchedulerLock(name = "findAllLoansCards", lockAtMostFor = "PT1H", lockAtLeastFor = "PT30M")
    public synchronized void findAllLoansCards() {
        log.info("Start finder All Loans Cards");

        loanDAO
                .findAllNonDeleted()
                .forEach(l -> {
                            log.info("Begin find cards of Loan: {}", l.getExtId());

                            humoCardAdderComponent.getCardFinderResponse(l);
                            uzCardAdderComponent.getCardFinderResponse(l);
                            masterCardAdderComponent.getCardFinderResponse(l);
                        }
                );

        log.info("Finished Card finder for All Loans!");
    }


    @Async
    @SchedulerLock(name = "findAutoPayCards", lockAtMostFor = "PT1H", lockAtLeastFor = "PT30M")
    @Deprecated
    public synchronized void findAutoPayCards() {
        log.info("Start finder autoPay Cards");


        loanDAO
                .findAllNonDeleted()
                .forEach(l -> {

                            if (l.getPinfl() == null || l.getPinfl().isEmpty()) {
                                log.error("Loan Debtor PINFL is null");
                                return;
                            }

                            log.info("Begin find cards of Loan: {}", l.getExtId());
                            uzCardAdderComponent.getAutoPayCards(l);
                        }
                );

        log.info("Finished AutoPay card finder for All Loans!");
    }


    @Scheduled(cron = "0 0 3 * * ?")
    @SchedulerLock(name = "findOnlyMasterCards", lockAtMostFor = "PT1H", lockAtLeastFor = "PT30M")
    public void findOnlyMasterCards() {
        log.info("Started scheduled master cards finder method...");

        loanDAO
                .findAllNonDeleted()
                .forEach(l -> {

                            if (l.getPinfl() == null || l.getPinfl().isEmpty()) {
                                log.error("Loan Debtor PINFL is null");
                                return;
                            }

                            log.info("Begin find cards of Loan: {}", l.getExtId());
                            masterCardAdderComponent.getCardFinderResponse(l);
                        }
                );

        log.info("Finished scheduled master card finder!");
    }

}
