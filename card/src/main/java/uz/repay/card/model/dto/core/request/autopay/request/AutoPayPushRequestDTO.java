package uz.repay.card.model.dto.core.request.autopay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoPayPushRequestDTO {
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("cardId")
    private String cardId;
    @JsonProperty("transType")
    private Integer transType;
    @JsonProperty("udate")
    private Long udate;
    @JsonProperty("time")
    private Long time;
    @JsonProperty("reqamt")
    private Long amount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("acctbal")
    public Long balance;
    @JsonProperty("conamt")
    public Long conamt;
}
