package uz.nodir.notification.mongo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Document(collection = "broker_record")
@AllArgsConstructor
@NoArgsConstructor
public class BrokerRecord {
    @Id
    private UUID id;

    @Indexed(expireAfter = "P1D")
    private LocalDateTime createdTime;

    @Indexed
    private String uniqueField;
}
