package nodir.paymo_add.feign.humoware.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnActiveResp {
    public String amount;
    public String merchant_name;
    public String merchant_id;
    public String card_type;
    public String acq_inst;
    public String point_code;
    public String auth_action_code;
    public String paymentRef;
    public String auth_action_code_final;
    public String auth_ref_number;
    public String ccy_code;
    public String paymentID;
    public String auth_time;
    public String expiry;
    public String pan;
    public String centre_id;
    public String terminal_id;
    public String status;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getAcq_inst() {
        return acq_inst;
    }

    public void setAcq_inst(String acq_inst) {
        this.acq_inst = acq_inst;
    }

    public String getPoint_code() {
        return point_code;
    }

    public void setPoint_code(String point_code) {
        this.point_code = point_code;
    }

    public String getAuth_action_code() {
        return auth_action_code;
    }

    public void setAuth_action_code(String auth_action_code) {
        this.auth_action_code = auth_action_code;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getAuth_action_code_final() {
        return auth_action_code_final;
    }

    public void setAuth_action_code_final(String auth_action_code_final) {
        this.auth_action_code_final = auth_action_code_final;
    }

    public String getAuth_ref_number() {
        return auth_ref_number;
    }

    public void setAuth_ref_number(String auth_ref_number) {
        this.auth_ref_number = auth_ref_number;
    }

    public String getCcy_code() {
        return ccy_code;
    }

    public void setCcy_code(String ccy_code) {
        this.ccy_code = ccy_code;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getAuth_time() {
        return auth_time;
    }

    public void setAuth_time(String auth_time) {
        this.auth_time = auth_time;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCentre_id() {
        return centre_id;
    }

    public void setCentre_id(String centre_id) {
        this.centre_id = centre_id;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
