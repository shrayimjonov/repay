package nodir.paymo_add.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardResponseGenesis {
    public String jsonrpc;
    public String id;

    public ErrorRes error;
    public ArrayList<Result> result;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ErrorRes getError() {
        return error;
    }

    public void setError(ErrorRes error) {
        this.error = error;
    }

    public ArrayList<Result> getResult() {
        return result;
    }

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    @Data
    public static class ErrorRes {
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Result {
        public String contractId;
        public String id;
        @JsonIgnore
        public String username;
        public String refNum;
        public String ext;
        public String pan;
        public String pan2;
        public String tranType;
        public String transType;
        public String date7;
        public String date12;
        public String amount;
        public String currency;
        public String stan;
        public String field38;
        public String field48;
        public String field91;
        public String merchantId;
        public String terminalId;
        public String resp;
        public String respText;
        public String respSV;
        public String status;
        public String reversal;

        public String getContractId() {
            return contractId;
        }

        public void setContractId(String contractId) {
            this.contractId = contractId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRefNum() {
            return refNum;
        }

        public void setRefNum(String refNum) {
            this.refNum = refNum;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public String getPan() {
            return pan;
        }

        public void setPan(String pan) {
            this.pan = pan;
        }

        public String getPan2() {
            return pan2;
        }

        public void setPan2(String pan2) {
            this.pan2 = pan2;
        }

        public String getTranType() {
            return tranType;
        }

        public void setTranType(String tranType) {
            this.tranType = tranType;
        }

        public String getTransType() {
            return transType;
        }

        public void setTransType(String transType) {
            this.transType = transType;
        }

        public String getDate7() {
            return date7;
        }

        public void setDate7(String date7) {
            this.date7 = date7;
        }

        public String getDate12() {
            return date12;
        }

        public void setDate12(String date12) {
            this.date12 = date12;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStan() {
            return stan;
        }

        public void setStan(String stan) {
            this.stan = stan;
        }

        public String getField38() {
            return field38;
        }

        public void setField38(String field38) {
            this.field38 = field38;
        }

        public String getField48() {
            return field48;
        }

        public void setField48(String field48) {
            this.field48 = field48;
        }

        public String getField91() {
            return field91;
        }

        public void setField91(String field91) {
            this.field91 = field91;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public String getResp() {
            return resp;
        }

        public void setResp(String resp) {
            this.resp = resp;
        }

        public String getRespText() {
            return respText;
        }

        public void setRespText(String respText) {
            this.respText = respText;
        }

        public String getRespSV() {
            return respSV;
        }

        public void setRespSV(String respSV) {
            this.respSV = respSV;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReversal() {
            return reversal;
        }

        public void setReversal(String reversal) {
            this.reversal = reversal;
        }
    }

}
