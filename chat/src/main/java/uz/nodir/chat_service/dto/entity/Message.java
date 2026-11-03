package uz.nodir.chat_service.dto.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.nodir.chat_service.dto.domain.enums.MessageStatus;
import uz.nodir.chat_service.dto.domain.enums.Sender;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table
@NoArgsConstructor
public class Message {
    @SequenceGenerator(name = "message_seq",
            sequenceName = "message_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "message_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String text;
    private String senderName;
    @Enumerated(EnumType.STRING)
    private Sender sender;
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    private String recipientName;
    private Long recipientId;
    private LocalDateTime readTime;
    private Long chatId;



}
