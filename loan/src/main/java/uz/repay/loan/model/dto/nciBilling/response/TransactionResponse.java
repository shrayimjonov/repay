package uz.repay.loan.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {
    @JsonProperty("id")
    public String id;
    @JsonProperty("branch")
    public String branch;
    @JsonProperty("acc_CL")
    public String clientAccount;
    @JsonProperty("name_CL")
    public String clientName;
    @JsonProperty("acc_CO")
    public String correspondenceAccount;
    @JsonProperty("name_CO")
    public String correspondenceName;
    @JsonProperty("purpose")
    public String purpose;
    @JsonProperty("summa")
    public String amount;
    @JsonProperty("v_DATE")
    public String date;
    @JsonProperty("status")
    public String status;
}
