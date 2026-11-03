package uz.repay.payment.model.dto.humo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.utils.CardUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoBasePaymentResponse {
    private String merchant_id;
    private String auth_action_code_final;
    private Long amount1a;
    private String paymentRef;
    private Integer ccy_code;
    private String auth_time;
    private Integer action;
    private String expiry;
    private String pan;
    private String centre_id;
    private Integer conversion_rate;
    private String terminal_id;
    private Long amount;
    private String reconcile_info;
    private String merchant_name;
    private String auth_action_code;
    private String auth_ref_number;
    private String internal_pan_masked;
    private Long cardholder_amount1a;
    private String paymentID;
    private Long amount1a_original;
    private String status;
    private CoreError error;

    public String getPan() {
        if (this.pan != null)
            return CardUtils.maskPanDigits(this.pan);
        return null;
    }
}
