package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.entity.EPos;
import uz.repay.card.model.entity.Guarantor;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.util.LoanUtils;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SVGateAutoPayCardsParam {
    @JsonProperty("allinfo")
    private SVGateAutoPayClientInfo clientInfo;

    public SVGateAutoPayCardsParam(String loanExtId, Guarantor guarantor, LoanInfoResponse result, EPos ePos) {
        this.clientInfo = new SVGateAutoPayClientInfo(loanExtId, guarantor, result, ePos);
    }

    public Map<Object, Object> toMap() {
        var request = new HashMap<>();
        request.put("clientInfo", clientInfo);
        return request;
    }

    public SVGateAutoPayCardsParam(Loan loan, LoanInfoResponse result, EPos ePos) {
        this.clientInfo = new SVGateAutoPayClientInfo(loan, result, ePos);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SVGateAutoPayClientInfo {
        @JsonProperty("pinfl")
        private String pinfl;
        @JsonProperty("owner")
        private String owner;
        @JsonProperty("loanId")
        private String loanId;
        @JsonProperty("contractNumber")
        private String contractNumber;
        @JsonProperty("contractDateStart")
        private Integer contractDateStart;
        @JsonProperty("contractDateEnd")
        private Integer contractDateEnd;
        @JsonProperty("creditType")
        private Integer creditType;
        @JsonProperty("creditSource")
        private String creditSource;
        @JsonProperty("creditDestination")
        private String creditDestination;
        @JsonProperty("subscribe")
        private Integer subscribe;
        @JsonProperty("merchantId")
        private String merchantId;
        @JsonProperty("terminalId")
        private String terminalId;

        public SVGateAutoPayClientInfo(Loan loan, LoanInfoResponse result, EPos ePos) {
            this.pinfl = loan.getPinfl();
            this.owner = loan.getPayerName();
            this.loanId = loan.getExtId();
            this.contractNumber = loan.getExtId();
            this.contractDateStart = Integer.valueOf(result.getLoanOpen().replaceAll("-", ""));
            this.contractDateEnd = Integer.valueOf(result.getLoanClose().replaceAll("-", ""));
            this.creditType = LoanUtils.convert(result.getLoanType()).getCode();
            this.creditSource = "bank";
            this.creditDestination = "account:" + result.getTransitAccount();
            this.subscribe = 1; // 1 - enable 0 - disable
            this.merchantId = ePos.getMerchantId();
            this.terminalId = ePos.getTerminalId();
        }

        public SVGateAutoPayClientInfo(String loanExtId, Guarantor guarantor, LoanInfoResponse result, EPos ePos) {
            this.pinfl = guarantor.getPersonCode();
            this.owner = guarantor.getSurname() + " " + guarantor.getFirstName() + " " + guarantor.getPatronymic();
            this.loanId = loanExtId;
            this.contractNumber = loanExtId;
            this.contractDateStart = Integer.valueOf(result.getLoanOpen().replaceAll("-", ""));
            this.contractDateEnd = Integer.valueOf(result.getLoanClose().replaceAll("-", ""));
            this.creditType = LoanUtils.convert(result.getLoanType()).getCode();
            this.creditSource = "bank";
            this.creditDestination = "account:" + result.getTransitAccount();
            this.subscribe = 1; // 1 - enable 0 - disable
            this.merchantId = ePos.getMerchantId();
            this.terminalId = ePos.getTerminalId();
        }
    }
}
