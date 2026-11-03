package uz.repay.card.model.dto.master.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterBalanceResponse {
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("token")
    private String token;
    @JsonProperty("username")
    private String username;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("balance")
    private Long balance;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("holder")
    private String holder;
    @JsonProperty("pinCount")
    private Integer pinCount;
    @JsonProperty("customer")
    private String customer;
    @JsonProperty("account")
    private String account;
    @JsonProperty("currency")
    private String currency;
}
