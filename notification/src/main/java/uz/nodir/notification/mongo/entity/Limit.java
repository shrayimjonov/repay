package uz.nodir.notification.mongo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.nodir.notification.model.enums.TemplateType;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@Document(collection = "limit")
@AllArgsConstructor
@NoArgsConstructor
public class Limit {
    @Id
    private UUID id;

    @Indexed(expireAfter = "P1D")
    private LocalDateTime createdTime;

    @Indexed
    private String phone;

    @Indexed
    private String loanExtId;

    @Indexed
    private TemplateType templateType;
}
