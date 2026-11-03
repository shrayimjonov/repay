package uz.repay.payment.model.dto.visa.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisaHistoryResult {
    @JsonProperty("REC_DATE")
    private Date recDate;
    @JsonProperty("CTIME")
    private Date cTime;
    @JsonProperty("TRAN_TYPE")
    private String tranType;
    @JsonProperty("INTERNAL_NO")
    private String internalNumber;
    @JsonProperty("CARD")
    private String card;
    @JsonProperty("MERCHANT")
    private String merchant;
    @JsonProperty("TRAN_AMT")
    private String amount;
    @JsonProperty("ADD_INFO")
    private String addInfo;
    @JsonProperty("AMOUNT_NET")
    private String amountNet;
    @JsonProperty("TRAN_CCY")
    private String tranCCY;
    @JsonProperty("ABVR_NAME")
    private String abvrName;
}
