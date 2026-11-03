package uz.nodir.notification.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.nodir.notification.mongo.entity.BrokerRecord;
import uz.nodir.notification.service.business.MessageService;
import uz.nodir.notification.service.business.PushService;
import uz.nodir.notification.service.core.BrokerRecordDAO;
import uz.nodir.repayautoconfigure.service.RecordToClassConverter;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.CardAttachedEventDTO;
import uz.repay.common.model.dto.request.kafka.CardConfirmationEventDTO;
import uz.repay.common.model.dto.request.kafka.LoanPaidEventDTO;
import uz.repay.common.model.dto.request.kafka.NotificationDebitPayEventDTO;
import uz.repay.common.model.dto.request.kafka.base.BaseEventDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.DateUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerComponent {
    private final PushService pushService;
    private final MessageService messageService;
    private final BrokerRecordDAO brokerRecordDAO;
    private final RecordToClassConverter recordToClassConverter;
    private static final Integer KAFKA_NACK_TIME = 600;

    @KafkaListener(topics = Topics.PAYMENT_TRANSACTION_PUSH_NOTIFICATION_STREAMS, groupId = "notification-push")
    public void handlePayNotification(@Payload String value, Acknowledgment acknowledgment) {
        log.info("Received pay notification event dto: {}", value);
        acknowledgment.acknowledge();

        var convertResult = recordToClassConverter.convertMessageV2(value, LoanPaidEventDTO.class);

        if (!convertResult.getStatus()) {
            log.error("Convert result is unsuccessful and will be returned");
            return;
        }

        pushService.receiveLoanDebit(convertResult.getData());

        log.info("Finished event dto: {}", convertResult.getData());
    }

    @KafkaListener(topics = Topics.PAYMENT_TRANSACTION_SMS_NOTIFICATION_STREAMS, groupId = "notification-debit-pay")
    public void handleDebitPay(ConsumerRecord<String, String> value, Acknowledgment acknowledgment) {
        log.info("Received debit pay event dto: {}", value);

        var eventDTOResult = eventValidation(acknowledgment, value, NotificationDebitPayEventDTO.class);

        if (!eventDTOResult.getStatus())
            return;

        var eventDTO = eventDTOResult.getData();

        if (!(eventDTO instanceof NotificationDebitPayEventDTO requestDTO)) {
            log.error("event DTO is not instanceof {}", NotificationDebitPayEventDTO.class);
            return;
        }

        var debitPayResult = messageService.debitPay(requestDTO);

        processResult(acknowledgment, debitPayResult, eventDTO);
    }

    private void validateRecordFraud(Acknowledgment acknowledgment, String... values) {
        var builder = new StringBuilder();
        Arrays.stream(values).forEach(builder::append);
        String brRecordValue = builder.toString();

        var list = brokerRecordDAO.findByUniqueField(brRecordValue);

        if (list.isEmpty() || list.size() < 3) {
            log.info("Broker record size: {} and will be created", list.size());
            createNewBR(brRecordValue);
            acknowledgment.nack(Duration.ofSeconds(KAFKA_NACK_TIME));
        } else {
            acknowledgment.acknowledge();
            log.error("Event takes error 3 times and will be removed from broker! Value: {}", brRecordValue);
        }
    }


    private SimpleResponse<BrokerRecord> createNewBR(String value) {
        var br = new BrokerRecord(
                UUID.randomUUID(),
                DateUtils.getDateTimeNow(),
                value
        );
        var brokerRecord = brokerRecordDAO.save(br);
        return new SimpleResponse<>(brokerRecord);
    }

    @KafkaListener(topics = Topics.CARD_CONFIRMATION, groupId = "notification-card-confirm")
    public void handleCardConfirmation(@Payload String value, Acknowledgment acknowledgment) {
        log.info("Received card confirmation notification event dto: {}", value);
        acknowledgment.acknowledge();

        var convertResult = recordToClassConverter.convertMessage(value, CardConfirmationEventDTO.class);

        if (!convertResult.getStatus()) {
            log.error("Convert result is unsuccessful and will be returned");
            return;
        }

        messageService.confirmation(convertResult.getData());

        log.info("Finished event dto: {}", convertResult.getData());
    }


    @KafkaListener(topics = Topics.CARD_ADD_NOTIFICATION_STREAM, groupId = "notification-card-attached")
    public void handleCardAttached(ConsumerRecord<String, String> value, Acknowledgment acknowledgment) {
       /* log.info("Received card attached event dto: {}", value);
        var eventDTOResult = eventValidation(acknowledgment, value, CardAttachedEventDTO.class);

        if (!eventDTOResult.getStatus())
            return;

        var eventDTO = eventDTOResult.getData();


        if (!(eventDTO instanceof CardAttachedEventDTO requestDTO)) {
            log.error("event DTO is not instanceof {}", CardAttachedEventDTO.class);
            return;
        }

        var debitPayResult = messageService.cardAttach(requestDTO);

        processResult(acknowledgment, debitPayResult, eventDTO);*/
    }


    public SimpleResponse<? extends BaseEventDTO> eventValidation(Acknowledgment acknowledgment,
                                                                  ConsumerRecord<String, String> value,
                                                                  Class<? extends BaseEventDTO> clazz) {
        var convertResult = recordToClassConverter.convertMessageV2(value.value(), clazz);

        if (!convertResult.getStatus()) {
            acknowledgment.acknowledge();
            log.error("Convert result is unsuccessful and will be returned");
            return new SimpleResponse<>(convertResult.getError().getCode(), convertResult.getError().getMessage());
        }

        var dto = convertResult.getData();

        if (dto.getPhone() == null || dto.getPhone().isEmpty() || dto.getPhone().equals("null")) {
            var message = "Phone is null for loan: %s".formatted(dto.getLoanExtId());
            log.error(message);
            acknowledgment.acknowledge();
            return new SimpleResponse<>();
        }

        return new SimpleResponse<>(convertResult.getData());
    }


    public void processResult(Acknowledgment acknowledgment, ResultData<?> resultData, BaseEventDTO baseEventDTO) {
        if (resultData.getIsSuccess()
                || resultData.getError().getCode().equals(ResultState.LIMIT_REACHED.getCode())
                || resultData.getError().getCode().equals(ResultState.INCORRECT_PHONE.getCode()))
            acknowledgment.acknowledge();
        else
            validateRecordFraud(
                    acknowledgment,
                    baseEventDTO.fieldValues()
            );


        log.info("Finished event dto: {} with status: {}", baseEventDTO, resultData.getStatus());
    }
}
