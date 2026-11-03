package uz.nodir.chat_service.service.business.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import uz.nodir.chat_service.config.WSDestination;
import uz.nodir.chat_service.dto.domain.core.request.NotificationEvent;
import uz.nodir.chat_service.service.web.LoanClientService;
import uz.repay.common.config.kafka.Topics;
import uz.repay.common.model.dto.request.kafka.LoanPaidEventDTO;
import uz.repay.common.model.dto.response.core.SimpleResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerComponent {
    private final SimpMessagingTemplate template;
    private final LoanClientService loanClientService;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = Topics.PAY_NOTIFICATION, groupId = "chat-notification")
    public void handleUserNotifications(@Payload String value) {
        log.info("Received event dto: {}", value);
        var convertResult = convertMessage(value, LoanPaidEventDTO.class);

        if (!convertResult.getStatus()) {
            log.error("Convert result is unsuccessful and will be returned");
            return;
        }

        var loanExtId = convertResult.getData().getLoanExtId();

        var loanResult = loanClientService.takeLoanFeaturedCreators(loanExtId);

        if (!loanResult.getStatus()) {
            log.error("Loan: {} result  is unsuccessful", loanExtId);
            return;
        }

        if (loanResult.getData().getError() != null) {
            log.error("Loan Client has error: {}", loanResult.getData().getError().getMessage());
            return;
        }

        var loans = loanResult.getData().getResult();

        if (loans.isEmpty()) {
            log.warn("Loan: {} don't added to featured list to any users", loanExtId);
            return;
        }

        //ws://localhost:8716/chat/ws/user/user@example.com/notifications
        var request = new NotificationEvent(
                "У вас новое уведомление по кредиту: %s".formatted(loanExtId)
        );

        loans
                .forEach(l -> {
                            log.info("Sending pay notification to user: {}", l);
                            template.convertAndSendToUser(l, WSDestination.USER_NOTIFICATION, request);
                        }
                );


        log.error("Finished event dto: {}", convertResult.getData());
    }


    private <T> SimpleResponse<T> convertMessage(String message, Class<T> clazz) {
        try {
            return new SimpleResponse<>(mapper.readValue(message, clazz));
        } catch (Exception e) {
            log.error("mapper converting error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }


}
