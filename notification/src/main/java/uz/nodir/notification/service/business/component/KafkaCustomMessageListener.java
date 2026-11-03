package uz.nodir.notification.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.nodir.notification.service.business.CustomMessageService;
import uz.nodir.repayautoconfigure.service.RecordToClassConverter;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.CourtEventDTO;


@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCustomMessageListener {
    private final RecordToClassConverter recordToClassConverter;
    private final CustomMessageService customMessageService;

    @KafkaListener(topics = Topics.NOTIFICATION_COURT_ALERT, groupId = "notification-court-alert")
    public void handleCourtAlertNotification(@Payload String value, Acknowledgment acknowledgment) {
        log.info("Received court alert notification event dto: {}", value);
        acknowledgment.acknowledge();

        var convertResult = recordToClassConverter.convertMessage(value, CourtEventDTO.class);

        if (!convertResult.getStatus()) {
            log.error("Convert result is unsuccessful and will be returned");
            return;
        }

        var result = customMessageService.courtMessage(convertResult.getData());

        if (!result.getIsSuccess()) {
            log.error("Finished method for court alert with error: {}", result.getError());
            return;
        }

        log.info("Finished method for court alert with SUCCESS status");
    }
}
