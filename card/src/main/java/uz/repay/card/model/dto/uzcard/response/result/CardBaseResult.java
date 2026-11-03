package uz.repay.card.model.dto.uzcard.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardBaseResult {
    @JsonProperty("id")
    String id;
    @JsonProperty("username")
    String username;
    @JsonProperty("pan")
    String pan;
    @JsonProperty("expiry")
    String expiry;
    @JsonProperty("status")
    Integer status;
    @JsonProperty("phone")
    String phone;
    @JsonProperty("fullName")
    String fullName;
    @JsonProperty("balance")
    Long balance;
    @JsonProperty("sms")
    Boolean sms;
    @JsonProperty("pincnt")
    Integer pinCount;
    @JsonProperty("cardtype")
    String cardType;
    @JsonProperty("holdAmount")
    Long holdAmount;
    @JsonProperty("cashbackAmount")
    Long cashbackAmount;
    @JsonProperty("aacct")
    String account;
    @JsonProperty("par")
    String par;
}
