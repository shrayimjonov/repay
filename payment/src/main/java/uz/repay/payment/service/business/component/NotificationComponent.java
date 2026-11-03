package uz.repay.payment.service.business.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.service.broker.KafkaProducer;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.LoanPaidEventDTO;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.DateUtils;
import uz.repay.payment.mongo.entity.FailedBrokerData;
import uz.repay.payment.service.business.NotificationService;
import uz.repay.payment.service.core.FailedBrokerDataMongoDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationComponent {
    private final KafkaProducer kafkaProducer;
    private final NotificationService notificationService;
    private final FailedBrokerDataMongoDAO failedBrokerDataMongoDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @Deprecated
    public void sendNotification(String phone, String fullName, String loanExtId,
                                 Long amount, String pan, Processing processing,
                                 String createdBy) {
        log.info("Send notification component take params: {} {} {} {} {} {}",
                phone, fullName, loanExtId, amount, pan, processing);
        if (!processing.equals(Processing.SVGATE_AUTOPAY)) //cause autoPay cards phone is fake -> 99890 123 45 67
            notificationService.sendMessageAfterDebit(
                    phone,
                    fullName,
                    loanExtId,
                    amount,
                    pan
            );

        var loanPaidEventDTO = new LoanPaidEventDTO(
                loanExtId,
                pan,
                amount,
                createdBy
        );
        kafkaProducer.send(Topics.PAY_NOTIFICATION, loanPaidEventDTO.toJson());
        log.info("Send notification finished!");
    }

    @Async
    public void sendNotificationToBroker(String phone, String fullName, String loanExtId,
                                         Long amount, String pan, Processing processing,
                                         String createdBy, Payer payer, Language language) {
        log.info("Send notification component take params: {} {} {} {} {} {}",
                phone, fullName, loanExtId, amount, pan, processing);


        Map<Object, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("fullName", fullName);
        map.put("loanExtId", loanExtId);
        map.put("amount", amount);
        map.put("pan", pan);
        map.put("processing", processing);
        map.put("createdBy", createdBy);
        map.put("payer", payer);
        map.put("language", language);

        try {
            kafkaProducer.send(Topics.PAYMENT_TRANSACTION_TOPIC, objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            log.error("Occurred error while convert values to json with message: {}", e.getMessage());
        }


        log.info("Send notification v2 is finished!");
    }

    @Deprecated
    private void checkBrokerResponse(String phone, String loanExtId,
                                     SimpleResponse<CompletableFuture<SendResult<String, String>>> brokerResult,
                                     String topic, String json) {
        brokerResult.getData()
                .whenComplete(
                        (result, throwable) -> {

                            if (throwable != null) {
                                log.error("Broker result is unsuccessful with throw message: {}", throwable.getMessage());

                                var failedBrokerData = new FailedBrokerData(
                                        UUID.randomUUID(),
                                        DateUtils.getDateTimeNow(),
                                        topic,
                                        json,
                                        phone,
                                        loanExtId,
                                        false
                                );

                                failedBrokerDataMongoDAO.save(failedBrokerData);
                                return;
                            }


                            var metadata = result.getRecordMetadata();
                            var partition = metadata.partition();
                            var offset = metadata.offset();

                            log.info("Sent message to partition: {} with offset={}", partition, offset);
                        }
                );
    }
}
