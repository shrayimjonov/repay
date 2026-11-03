package uz.repay.card.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.humo.HumoBalanceDetectedEvent;
import uz.repay.card.service.business.CardBalanceAlertService;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.utils.CardUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class HumoBalanceScanProcessor {
    private final CardBalanceAlertService cardBalanceAlertService;

    public void process(HumoBalanceDetectedEvent event, int partition) {
        if (!isValid(event)) {
            log.warn("event=humo.balance.detected.consumer.skipped reason=invalid_event partition={} cardId={}",
                    partition, event != null ? event.getCardId() : null);
            return;
        }

        var startNanos = System.nanoTime();
        try {
            var pan = CardUtils.decryptPan(event.getEncryptedPan());
            if (pan.isBlank()) {
                log.warn("event=humo.balance.detected.consumer.skipped reason=empty_pan cardId={}", event.getCardId());
                return;
            }

            cardBalanceAlertService.debitFromCard(pan, event.getAvailableAmount(), AddType.AUTO, CardType.pan);
            log.info("event=humo.balance.detected.consumer.completed cardId={} maskedPan={} partition={} amount={} durationMs={} detectionLagMs={}",
                    event.getCardId(), event.getMaskedPan(), partition, event.getAvailableAmount(),
                    elapsedMillis(startNanos), detectionLagMillis(event));
        } catch (RuntimeException error) {
            log.error("event=humo.balance.detected.consumer.exception cardId={} maskedPan={} partition={} message={}",
                    event.getCardId(), event.getMaskedPan(), partition, error.getMessage(), error);
        }
    }

    private boolean isValid(HumoBalanceDetectedEvent event) {
        return event != null
                && event.getCardId() != null
                && event.getEventId() != null
                && !event.getEventId().isBlank()
                && event.getEncryptedPan() != null
                && !event.getEncryptedPan().isBlank()
                && event.getAvailableAmount() != null
                && event.getAvailableAmount() > 0;
    }

    private long detectionLagMillis(HumoBalanceDetectedEvent event) {
        return event.getDetectedAtMillis() != null
                ? Math.max(0L, System.currentTimeMillis() - event.getDetectedAtMillis())
                : 0L;
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
