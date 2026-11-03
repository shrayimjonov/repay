package uz.repay.card.service.business.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.service.RecordToClassConverter;
import uz.repay.card.model.dto.backlash.BacklashEventDTO;
import uz.repay.card.model.dto.backlash.CardDeactivationDTO;
import uz.repay.card.service.business.CardDeactivationService;
import uz.repay.card.service.core.SwitcherRedisService;
import uz.repay.card.service.web.PaymentClientService;
import uz.repay.common.config.kafka.Topics;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardSwitcherDisableComponent {
    private final PaymentClientService paymentClientService;
    private final SwitcherRedisService switcherRedisService;
    private final CardDeactivationService cardDeactivationService;
    private final RecordToClassConverter recordToClassConverter;

    @EventListener
    public void handleSwitchProcessing(@NonNull BacklashEventDTO backlashEventDTO) {
        log.info("Try to disable card processing: {}", backlashEventDTO);

//         var switcher = switcherRedisService.findByName(backlashEventDTO.getProcessing().name());
//
//             log.info("Found switcher for processing: {}", switcher);
//
//         switcherRedisService.updateStatus(switcher.getId(), false);
//
//           log.info("Switcher disabled, next is create backlash...");
//
//        paymentClientService.createBacklash(
//                new BacklashCreateRequestDTO(
//                        "0000000000",
//                        backlashEventDTO.getSpentTime(),
//                        backlashEventDTO.getProcessing(),
//                        StatusType.CARD
//                )
//        );
    }

    @EventListener
    @Deprecated
    public void handleNotFoundCards(@NonNull CardDeactivationDTO deactivationDTO) {
        log.info("Begin card deactivate event listener with request: {}", deactivationDTO);
        cardDeactivationService.checkCardToDeactivate(deactivationDTO.getCardId());
    }

    @KafkaListener(topics = Topics.CARD_DEACTIVATE_TOPIC, groupId = "card-not-found-cards-deactivate")
    public void handleNotFoundCards(final ConsumerRecord<String, String> record) {
        log.info("Begin card deactivate kafka listener with record: {}", record);

        var convertResult = recordToClassConverter.convertMessageV2(record.value(), CardDeactivationDTO.class);

        if (!convertResult.getStatus()) {
            log.error("Convert result is unsuccessful and will be returned");
            return;
        }

        var deactivationDTO = convertResult.getData();

        cardDeactivationService.checkCardToDeactivate(deactivationDTO.getCardId());

        log.info("Finished card deactivate kafka listener for: {}", deactivationDTO);
    }
}
