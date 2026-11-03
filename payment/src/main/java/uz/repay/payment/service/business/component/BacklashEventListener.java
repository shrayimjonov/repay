package uz.repay.payment.service.business.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uz.repay.payment.model.dto.core.backlash.BacklashEventDTO;
import uz.repay.payment.service.core.BacklashService;
import uz.repay.payment.service.core.SwitcherRedisService;

@Component
@Slf4j
@RequiredArgsConstructor
public class BacklashEventListener {
    private final BacklashService backlashService;
    private final SwitcherRedisService switcherRedisService;

    @EventListener
    public void handleBackLashEvent(@NonNull BacklashEventDTO event) {
        log.info("Handling backlash event: {}", event);

        backlashService.create(event.getLoanExtId(), event.getProcessing(), event.getSpentTime(), event.getType());

        log.warn("Try to disable: {} processing after un success payment", event.getProcessing());

//        switcherRedisService.switchProcessing(event.getProcessing(), false);
    }
}
