package uz.repay.payment.model.dto.uzcard.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransPayPurposeResult {
    private String id;
    private String username;
    private String refNum;
    private String ext;
    private String pan;
    private String tranType;
    private String date7;
    private String date12;
    private Long amount;
    private String currency;
    private String stan;
    private String merchantId;
    private String terminalId;
    private Integer resp;
    private String respText;
    private String respSV;
    private String status;
    private String respDesc;
}
