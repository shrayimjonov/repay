package uz.repay.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.utils.HttpServletUtils;
import uz.repay.payment.model.dto.billing.request.PayRequest;
import uz.repay.payment.model.enums.TranType;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(
        name = "bill",
        indexes = {
                @Index(name = "bill_tran_index", columnList = "tran_id")
        })
@AllArgsConstructor
@NoArgsConstructor
public class Bill extends BaseEntity {
    @SequenceGenerator(name = "bill_seq",
            sequenceName = "bill_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "bill_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "request")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Object, Object> request;

    @Column(name = "response")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Object, Object> response;

    @Column(name = "credit_to")
    private String creditTo; // 17409%

    @Column(name = "ref_num")
    private String refNum;

    @Column(name = "tran_type")
    @Enumerated(EnumType.STRING)
    private TranType tranType;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ResponseStatus status;

    @ManyToOne
    @JoinColumn(name = "tran_id")
    private Transaction tranId; //transaction table id

    @Column(name = "billing_id")
    private Long billingId; //id from billing

    @Column(name = "terminal_id")
    private String terminalId;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "debit_from")
    private String debitFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "ip_address")
    private String ipAddress;

    @Deprecated
    public Bill(Long amount, String cardPan, Transaction transaction) {
        this.amount = amount;
        this.debitFrom = cardPan;
        this.status = ResponseStatus.CHECKING;
        this.tranType = TranType.CHECK;
        this.tranId = transaction;
    }

    @Deprecated
    public Bill(PayRequest preparedPayRequest,
                Bill bill,
                String loanId) {
        this.amount = bill.getAmount();
        this.debitFrom = bill.getCreditTo();
        this.status = ResponseStatus.PENDING;
        this.tranType = TranType.DEBIT;
        this.tranId = bill.getTranId();
        this.request = preparedPayRequest.toMap();
        this.requestId = bill.getRequestId();
        this.creditTo = loanId;
        this.terminalId = bill.getTerminalId();
        this.merchantId = bill.getMerchantId();
        this.billingId = bill.getBillingId();
    }

    @Deprecated
    public Bill(UUID requestId,
                Map<Object, Object> request,
                String creditTo,
                String refNum,
                TranType tranType,
                Long amount,
                ResponseStatus status,
                Transaction tranId,
                Long billingId,
                String terminalId,
                String merchantId,
                String debitFrom) {
        this.requestId = requestId;
        this.request = request;
        this.creditTo = creditTo;
        this.refNum = refNum;
        this.tranType = tranType;
        this.amount = amount;
        this.status = status;
        this.tranId = tranId;
        this.billingId = billingId;
        this.terminalId = terminalId;
        this.merchantId = merchantId;
        this.debitFrom = debitFrom;
    }


    public Bill(Long amount,
                String debitFrom,
                Transaction preparedTransaction,
                ResponseStatus status,
                TranType tranType,
                Currency currency) {
        this.amount = amount;
        this.debitFrom = debitFrom;
        this.status = status;
        this.tranType = tranType;
        this.tranId = preparedTransaction;
        this.currency = currency;
        this.ipAddress = HttpServletUtils.getRequestIPAddress();
    }


    public Bill(Long amount,
                String creditTo, String debitFrom,
                String terminalId, String merchantId,
                Transaction transaction, ResponseStatus status,
                TranType tranType, Map<Object, Object> request,
                Currency currency) {
        this.amount = amount;
        this.creditTo = creditTo;
        this.debitFrom = debitFrom;
        this.terminalId = terminalId;
        this.merchantId = merchantId;
        this.status = status;
        this.tranType = tranType;
        this.tranId = transaction;
        this.request = request;
        this.currency = currency;
        this.ipAddress = HttpServletUtils.getRequestIPAddress();
    }

    public Bill(Long amount, String debitFrom, String creditTo,
                String refNum, String merchantId, String terminalId,
                TranType tranType, ResponseStatus status,
                Map<Object, Object> request, Transaction transaction,
                Currency currency) {
        this.amount = amount;
        this.creditTo = creditTo;
        this.debitFrom = debitFrom;
        this.refNum = refNum;
        this.terminalId = terminalId;
        this.merchantId = merchantId;
        this.status = status;
        this.tranType = tranType;
        this.tranId = transaction;
        this.request = request;
        this.currency = currency;
        this.ipAddress = HttpServletUtils.getRequestIPAddress();
    }


    public Bill(Long amount, String accountCredit, String accountDebit,
                Transaction transaction, ResponseStatus responseStatus, TranType tranType,
                Currency currency) {
        this.amount = amount;
        this.creditTo = accountCredit;
        this.debitFrom = accountDebit;
        this.tranId = transaction;
        this.status = responseStatus;
        this.tranType = tranType;
        this.currency = currency;
        this.ipAddress = HttpServletUtils.getRequestIPAddress();
    }
}
