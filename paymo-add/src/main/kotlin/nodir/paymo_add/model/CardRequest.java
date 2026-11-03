package nodir.paymo_add.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    public Params params;
    public String customMethodName;
    public String jsonrpc;
    public String id;
    public String method;
    public String methodName;

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getCustomMethodName() {
        return customMethodName;
    }

    public void setCustomMethodName(String customMethodName) {
        this.customMethodName = customMethodName;
    }

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public static class Params{
        public Tran tran;

        public Tran getTran() {
            return tran;
        }

        public void setTran(Tran tran) {
            this.tran = tran;
        }
    }


    public static class Tran{
        public Long amount;
        public String cardId;
        public String ext;
        public int stan;
        public String port;
        public String merchantId;
        public String terminalId;
        public Object contractId;

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public int getStan() {
            return stan;
        }

        public void setStan(int stan) {
            this.stan = stan;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
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

        public Object getContractId() {
            return contractId;
        }

        public void setContractId(Object contractId) {
            this.contractId = contractId;
        }
    }
}
