package uz.repay.autodebit.model.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.autodebit.mongo.entity.PayResult;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayResultResponseDTO {
    private UUID id;
    private String branch;
    private Map<String, ResultData<?>> successList;
    private Map<String, ResultData<?>> failedList;
    private LocalDateTime createdTime;
    private String sessionId;

    public PayResultResponseDTO(PayResult payResult) {
        this.id = payResult.getId();
        this.branch = payResult.getBranch();
        this.successList = payResult.getSuccessList();
        this.failedList = payResult.getFailedList();
        this.createdTime = payResult.getCreatedTime();
        this.sessionId = payResult.getSessionId();
    }
}
