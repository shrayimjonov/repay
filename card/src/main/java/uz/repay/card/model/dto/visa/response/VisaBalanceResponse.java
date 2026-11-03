package uz.repay.card.model.dto.visa.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisaBalanceResponse {
    @JsonProperty("balance")
    private Long balance;
    @JsonProperty("pinCount")
    private Integer pinCount;
    @JsonProperty("holder")
    private String holder;
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("error")
    private CoreError error;
}
