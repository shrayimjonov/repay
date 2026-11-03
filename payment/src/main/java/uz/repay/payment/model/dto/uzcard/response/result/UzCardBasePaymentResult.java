package uz.repay.payment.model.dto.uzcard.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.payment.model.enums.UzCardStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UzCardBasePaymentResult {
    private String id;
    private String username;
    private String refNum;
    private String ext;
    private String pan;
    private String pan2;
    private String expiry;
    private String tranType;
    private Integer transType;
    private String date7;
    private String date12;
    private Long amount;
    private Integer currency;
    private String stan;
    private String field38;
    private String field48;
    private String field91;
    private String merchantId;
    private String terminalId;
    private Integer resp;
    private String respText;
    private String respSV;
    private UzCardStatus status;
    private Integer reversal;
}
