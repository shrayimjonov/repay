package uz.repay.card.model.dto.humoGateway.response;

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
public class HuMoRecoActiveResponse {
    @JsonProperty("paymentID")
    private String paymentID;
    @JsonProperty("ttl")
    private Integer timeToLive;
    @JsonProperty("terminal_id")
    private String terminalId;
    @JsonProperty("ttl_action")
    private String ttlAction;
    @JsonProperty("paymentRef")
    private String paymentRef;
    @JsonProperty("status")
    private String status;
    @JsonProperty("error")
    private CoreError error;
}
