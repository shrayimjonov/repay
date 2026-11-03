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
public class HuMoRecoConfirmResponse {
    @JsonProperty("credits_reversal_number")
    private Integer creditsReversalNumber;
    @JsonProperty("credits_reversal_amount")
    private Long creditsReversalAmount;
    @JsonProperty("debits_number")
    private Integer debitsNumber;
    @JsonProperty("debits_amount")
    private Long debitsAmount;
    @JsonProperty("reconcile_info")
    private Long reconcileInfo;
    @JsonProperty("paymentID")
    private String paymentID;
    @JsonProperty("ttl")
    private Integer ttl;
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
