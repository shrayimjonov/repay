package uz.repay.payment.model.dto.visa.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.payment.model.dto.visa.response.result.VisaHistoryResult;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisaHistoryResponse {
    @JsonProperty("details")
    private ArrayList<VisaHistoryResult> details;
    @JsonProperty("error")
    private CoreError error;
}
