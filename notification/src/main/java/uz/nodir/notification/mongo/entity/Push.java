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
@Document(collection = "push_data")
@AllArgsConstructor
@NoArgsConstructor
public class Push {
    @Id
    private UUID id;

    @Indexed(expireAfter = "P7D")
    private LocalDateTime createdTime;

    @Indexed
    private String loanExtId;

    private String pan;

    private Long amount;

    @Indexed
    private String username;
    
    private Boolean isRead;


}
