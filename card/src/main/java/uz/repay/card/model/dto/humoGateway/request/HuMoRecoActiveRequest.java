package uz.repay.card.model.dto.humoGateway.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoRecoActiveRequest {
    @JsonProperty("login")
    private String login;
    @JsonProperty("refNumber")
    private String refNumber;
    @JsonProperty("terminalId")
    private String terminalId;
    @JsonProperty("subSystem")
    private String subSystem;
}
