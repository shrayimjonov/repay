package uz.nodir.chat_service.dto.domain.core.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.chat_service.dto.domain.enums.EventType;
import uz.nodir.chat_service.dto.domain.enums.MessageStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatEvent {
    private Long chatId;
    private MessageStatus status;
    private EventType type;
    private String sender;
    private String recipient;
}
