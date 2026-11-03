package uz.repay.payment.mongo.entity;

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
@Document(collection = "payment_failed_broker_data")
@AllArgsConstructor
@NoArgsConstructor
public class FailedBrokerData {
    @Id
    private UUID id;
    @Indexed(expireAfter = "P10D")
    //expire after 10 days, Example: "P2D" = 2 days or "PT30M" = 30 minutes or "PT10S" = 10 seconds
    private LocalDateTime createdTime = DateUtils.getDateTimeNow();
    private String topic;
    private String json;
    private String phone;
    private String loanExtId;
    private Boolean isSentAgain;

}
