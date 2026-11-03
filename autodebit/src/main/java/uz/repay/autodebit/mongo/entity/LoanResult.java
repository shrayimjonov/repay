package uz.repay.autodebit.mongo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Document(collection = "loan_result")
@AllArgsConstructor
@NoArgsConstructor
public class LoanResult {
    @Id
    private UUID id;
    private Branch branch;
    @Field
    @Indexed
    private String loanExtId;
    @Indexed(expireAfterSeconds = 86400)
    private LocalDateTime createdTime = DateUtils.getDateTimeNow();
    @Indexed
    private String sessionId;
    @Indexed
    private UUID workerId;
    private String message;

    public LoanResult(UUID id,
                      Branch branch,
                      String loanExtId,
                      String errorMessage,
                      String sessionId,
                      UUID workerId) {
        this.id = id;
        this.branch = branch;
        this.loanExtId = loanExtId;
        this.message = errorMessage;
        this.sessionId = sessionId;
        this.workerId = workerId;
    }
}
