package uz.repay.card.mongo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.repay.common.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Document(collection = "card_data")
@AllArgsConstructor
@NoArgsConstructor
public class CardData {
    @Id
    private UUID id;
    @Indexed(expireAfter = "P10D") //expire after 10 days, Example: "P2D" = 2 days or "PT30M" = 30 minutes or "PT10S" = 10 seconds
    private LocalDateTime createdTime = DateUtils.getDateTimeNow();
    @Indexed
    private Long cardId;
    private String maskedPan;
    private String holder;
    private String token;

}
