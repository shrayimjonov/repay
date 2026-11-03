package uz.repay.common.model.dto.response.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Branch;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanShortV2ResponseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("ext_id")
    private String extId;
    @JsonProperty("debtor")
    private String debtor;
    @JsonProperty("branch")
    private Branch branch;

}
