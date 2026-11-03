package uz.repay.card.mongo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.DateUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@Document(collection = "paid_requests")
@AllArgsConstructor
@NoArgsConstructor
public class PaidRequest implements Serializable {

    @Id
    private UUID id;

    private Payer payer;

    @Indexed
    private Processing processing;

    @Indexed
    private String username;

    @Indexed(expireAfter = "P30D")
    private LocalDateTime createdTime = DateUtils.getDateTimeNow();

    @Indexed
    private String loanExtId;



}
