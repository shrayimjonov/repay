package uz.repay.autodebit.mongo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@Document(collection = "payResult")
@AllArgsConstructor
@NoArgsConstructor
public class PayResult {
    @Id
    private UUID id;
    private String branch;
    @Field
    private Map<String, ResultData<?>> successList;
    @Field
    private Map<String, ResultData<?>> failedList;
    private LocalDateTime createdTime = DateUtils.getDateTimeNow();
    @Indexed
    private String sessionId;

    public PayResult(UUID id, String branch,
                     Map<String, ResultData<?>> successList,
                     Map<String, ResultData<?>> failedList, String sessionId) {
        this.id = id;
        this.branch = branch;
        this.successList = successList;
        this.failedList = failedList;
        this.sessionId = sessionId;
    }
}
