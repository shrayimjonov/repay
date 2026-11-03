package uz.repay.payment.model.dto.nciBilling.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.Branch;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NCIReverseDTO {
    @Branch
    private String branch;
    private String generalId;

    public Map<Object, Object> toMap() {
        return Map.of(
                "branch", this.branch,
                "generalId", this.generalId
        );
    }
}
