package uz.repay.autodebit.model.result.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.autodebit.mongo.entity.LoanResult;
import uz.repay.common.model.enums.Branch;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanResultResult {
    private UUID id;
    private Branch branch;
    private String loanExtId;
    private LocalDateTime createdTime;
    private String sessionId;
    private UUID workerId;
    private String message;

    public LoanResultResult(LoanResult result) {
        this.id = result.getId();
        this.branch = result.getBranch();
        this.loanExtId = result.getLoanExtId();
        this.createdTime = result.getCreatedTime();
        this.sessionId = result.getSessionId();
        this.workerId = result.getWorkerId();
        this.message = result.getMessage();
    }
}
