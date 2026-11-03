package uz.repay.payment.model.dto.master.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ResponseStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterPaymentResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("extId")
    private String extId;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("status")
    private ResponseStatus status;
    @JsonProperty("username")
    private String username;
    @JsonProperty("transactionTime")
    private String transactionTime;
    @JsonProperty("terminalId")
    private String terminalId;
    @JsonProperty("merchantId")
    private String merchantId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("commission")
    private Long commission;
    @JsonProperty("refNum")
    private String refNum;
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("operation")
    private String operation;

}
