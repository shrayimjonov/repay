package uz.repay.loan.service.core.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class SchedulerComponent {
    private final DebtLoanUpdaterComponent debtLoanUpdaterComponent;
    private final DeleteLoanCheckerComponent deleteLoanCheckerComponent;

    @Scheduled(cron = "0 0 1 * * ?")
    @SchedulerLock(name = "refreshDebtors_new_task", lockAtMostFor = "PT2H", lockAtLeastFor = "PT1H")
    public void refreshDebtLoanList() {
        debtLoanUpdaterComponent.refreshDebtLoanList();
    }


    @Scheduled(cron = "0 0 3 * * ?")
    @SchedulerLock(name = "checkLoanToDeleted", lockAtMostFor = "PT10H", lockAtLeastFor = "PT8H")
    public void checkLoanToDeleted() {
        deleteLoanCheckerComponent.checkLoanToDeleted();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @SchedulerLock(name = "loanSaveAllDebtorsStats", lockAtMostFor = "PT5H", lockAtLeastFor = "PT2H")
    public void refreshDebtLoanStats() {
        debtLoanUpdaterComponent.saveDailyDebtLoanReport();
    }
}
