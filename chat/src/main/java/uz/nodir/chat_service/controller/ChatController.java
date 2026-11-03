package uz.nodir.chat_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.nodir.chat_service.config.WSDestination;
import uz.nodir.chat_service.dto.domain.core.request.ChatEvent;
import uz.nodir.chat_service.dto.domain.core.request.NotificationEvent;
import uz.nodir.chat_service.dto.domain.enums.EventType;
import uz.nodir.chat_service.dto.domain.enums.MessageStatus;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate template;


    @GetMapping("/hello")
    public ResponseEntity<?> sayHello() {
        var request = new NotificationEvent(
                "У вас новое уведомление по кредиту: %s".formatted("0112177777")
        );

        template.convertAndSendToUser(SecurityUtils.getUsername(), WSDestination.USER_NOTIFICATION, request);

        return ResponseEntity.ok(request);
    }

    @GetMapping("/helloToUser")
    public ResponseEntity<?> sayHelloToUser() {
        //ws://localhost:8716/chat/ws/user/user@example.com/notifications
        var request = new ChatEvent(System.currentTimeMillis(),
                MessageStatus.NEW,
                EventType.FROM_CLIENT,
                "user@example.com",
                "user@example.com");

        template.convertAndSendToUser(SecurityUtils.getUsername(), "/notifications", request);
        return ResponseEntity.ok(request);
    }
}
