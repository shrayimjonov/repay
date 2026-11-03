package uz.repay.payment.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uz.repay.payment.model.dto.core.fraud.FraudEventDTO;
import uz.repay.payment.service.core.FraudRedisService;

@Component
@Slf4j
@RequiredArgsConstructor
public class FraudRemoverListener {
    private final FraudRedisService fraudRedisService;

    @EventListener
    public void handleLastFraud(FraudEventDTO eventDTO) {
        log.info("Remove 1 fraud for loan: {} from fraud bucket", eventDTO.getLoanExtId());

        var fraudList = fraudRedisService.findByLoanExtIdAndPan(eventDTO.getLoanExtId(), eventDTO.getCardPan());

        log.info("Frauds size: {}", fraudList.size());

        if (fraudList.isEmpty()) {
            log.info("Fraud list is empty. Exit event listener...");
            return;
        }

        var lastSuccessFraud = fraudList.getLast();

        log.info("Delete last fraud: {}", lastSuccessFraud);

        fraudRedisService.deleteById(lastSuccessFraud.getId());
    }
}
