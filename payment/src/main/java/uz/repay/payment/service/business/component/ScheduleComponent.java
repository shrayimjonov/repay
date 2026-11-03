package uz.repay.payment.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.StatusType;
import uz.repay.common.utils.DateUtils;
import uz.repay.payment.model.entity.Backlash;
import uz.repay.payment.model.property.InternalProperty;
import uz.repay.payment.service.business.PaymentServiceV2;
import uz.repay.payment.service.business.TemporaryService;
import uz.repay.payment.service.core.BacklashService;
import uz.repay.payment.service.core.SwitcherRedisService;
import uz.repay.payment.service.core.TransactionDAO;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class ScheduleComponent {
    private final TemporaryService temporaryService;
    private final BacklashService backlashService;
    private final InternalProperty internalProperty;
    private final SwitcherRedisService switcherRedisService;
    private final TransactionDAO transactionDAO;
    private final PaymentServiceV2 paymentServiceV2;

    @Scheduled(fixedDelayString = "${internal.temporary.intervalInMinutes}")
    @SchedulerLock(name = "handleRecipientSuccessTransactions", lockAtMostFor = "PT5M", lockAtLeastFor = "PT4M")
    public void handleRecipientSuccessTransactions() {
        temporaryService.checkManualTransactionFromABSV2();
    }

    @Scheduled(fixedDelay = 600_000) // 10 minutes
    @SchedulerLock(name = "checkTurnedOffProcessingByTransaction", lockAtMostFor = "PT8M", lockAtLeastFor = "PT4M")
    public void checkTurnedOffProcessingByTransaction() {
        log.info("Checking processing turned off by backlash by transaction...");

        var backlashOptional = backlashService.findLastNotUsedBacklash(StatusType.TRANSACTION);

        if (backlashOptional.isEmpty()) {
            log.info("Backlash is empty");
            return;
        }


        var backlash = backlashOptional.get();

        var blockedUntil = backlash.getCreatedTime().plusMinutes(internalProperty.getPayment().getDisabledTime());
        enableSwitcher(backlash, blockedUntil);
    }

    @Scheduled(fixedDelay = 300_000) // 5 minutes
    @SchedulerLock(name = "checkTurnedOffProcessingByCard", lockAtMostFor = "PT4M", lockAtLeastFor = "PT2M")
    public void checkTurnedOffProcessingByCard() {
        log.info("Checking processing turned off by backlash by card pc...");

        var backlashOptional = backlashService.findLastNotUsedBacklash(StatusType.CARD);

        if (backlashOptional.isEmpty()) {
            log.info("Backlash is empty");
            return;
        }


        var backlash = backlashOptional.get();

        var blockedUntil = backlash.getCreatedTime().plusMinutes(internalProperty.getCard().getDisabledTime());

        enableSwitcher(backlash, blockedUntil);
    }

    private void enableSwitcher(Backlash backlash, LocalDateTime blockedUntil) {
        log.info("Blocked until: {}", blockedUntil);

        if (DateUtils.getDateTimeNow().isBefore(blockedUntil)) {
            log.info("Backlash of : {} the time has not yet come", backlash.getProcessing());
            return;
        }

        backlash.setIsUsed(true);
        backlash.setUpdatedTime(DateUtils.getDateTimeNow());
        backlashService.update(backlash);

        switcherRedisService.switchProcessing(backlash.getProcessing(), true);
        log.info("Processing: {} is enabled again!", backlash.getProcessing());
    }

    @Scheduled(cron = "0 0 5 ? * * ")
    @SchedulerLock(name = "handleHuMoDebitConfirms", lockAtMostFor = "PT30M", lockAtLeastFor = "PT15M")
    public void handleHuMoDebitConfirms() {
        ResponseStatus dcFailStatus = ResponseStatus.DEBIT_CONFIRM_NEED_CONFIRM;

        log.info("Starting handle failed Debit Confirm huMo transactions with: {} statuses", dcFailStatus);

        transactionDAO.getOrphanTransactions()
                .stream()
                .filter(z -> z.getStatus().equals(dcFailStatus))
                .forEach(t -> {
                            log.info("HuMo debit confirm handling transaction: {}", t);

                            paymentServiceV2.completeTransaction(
                                    t.getId()
                            );
                        }
                );

        log.info("Finished handling huMo failed debit confirm transactions...");
    }

    @Scheduled(cron = "0 0 4 ? * * ")
    @SchedulerLock(name = "handleNeedConfirms", lockAtMostFor = "PT30M", lockAtLeastFor = "PT15M")
    public void handleNeedConfirms() {
        log.info("Starting handle transactions with: NEED_CONFIRM statuses");

        transactionDAO.getOrphanTransactions()
                .stream()
                .filter(
                        z -> z.getStatus().equals(ResponseStatus.NEED_CONFIRM) ||
                                z.getStatus().equals(ResponseStatus.S_NEED_CONFIRM) ||
                                z.getStatus().equals(ResponseStatus.R_NEED_CONFIRM) ||
                                z.getStatus().equals(ResponseStatus.UZCARD_NEED_CONFIRM)
                )
                .forEach(t -> {
                            log.info("Handling transaction: {}", t);

                            paymentServiceV2.synchronize(
                                    t.getId()
                            );
                        }
                );

        log.info("Finished handling NEED_CONFIRM transactions...");
    }


    @Scheduled(cron = "0 0 3 ? * * ")
    @SchedulerLock(name = "handleHuMoActiveTransactions", lockAtMostFor = "PT30M", lockAtLeastFor = "PT15M")
    public void handleHuMoActiveTransactions() {
        log.info("Starting handle transactions without DEBIT_CONFIRM success bills");

        transactionDAO.getActiveTransactions()
                .forEach(t -> {
                            log.info("Handling transaction: {}", t);

                            paymentServiceV2.synchronize(
                                    t.getId()
                            );
                        }
                );

        log.info("Finished handling transactions without DEBIT_CONFIRM success bills...");
    }

}
