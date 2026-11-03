package uz.repay.payment.model.dto.core.transaction.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.enums.TranType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillResult {
    private Long id;
    private UUID requestId;
    private Map<Object, Object> request;
    private Map<Object, Object> response;
    private String creditTo;
    private String refNum;
    private TranType tranType;
    private Long amount;
    private ResponseStatus status;
    private Long billingId;
    private String terminalId;
    private String merchantId;
    private String debitFrom;
    private LocalDateTime initTime;
    private String createdBy;
    private Currency currency;
    private String ipAddress;

    public BillResult(Bill bill) {
        this.id = bill.getId();
        this.requestId = bill.getRequestId();
        this.request = bill.getRequest();
        this.response = bill.getResponse();
        this.creditTo = bill.getCreditTo();
        this.refNum = bill.getRefNum();
        this.tranType = bill.getTranType();
        this.amount = bill.getAmount();
        this.status = bill.getStatus();
        this.billingId = bill.getBillingId();
        this.terminalId = bill.getTerminalId();
        this.merchantId = bill.getMerchantId();
        this.debitFrom = bill.getDebitFrom();
        this.initTime = bill.getInitTime();
        this.createdBy = bill.getCreatedBy();
        this.currency = bill.getCurrency();
        this.ipAddress = bill.getIpAddress();
    }
}
