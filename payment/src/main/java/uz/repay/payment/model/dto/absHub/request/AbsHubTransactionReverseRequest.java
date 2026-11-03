package uz.repay.payment.model.dto.absHub.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.Branch;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbsHubTransactionReverseRequest {
    @Branch
    @JsonProperty("branch")
    private String branch;
    @NotBlank
    @JsonProperty("generalId")
    private String generalId;

    public Map<Object, Object> toMap() {
        return Map.of(
                "branch", this.branch,
                "generalId", this.generalId
        );
    }
}
