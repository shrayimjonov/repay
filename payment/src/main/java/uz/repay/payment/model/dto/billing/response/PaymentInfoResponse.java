package uz.repay.payment.model.dto.billing.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ResponseStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentInfoResponse {
    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;
    private Long id;
    private Long requestAmount;
    private Integer requestCcy;
    private String requestTrId;
    private String requestLogin;
    private String requestGroup;
    private String senderName;
    private String senderAccount;
    private Long senderServiceId;
    private Long senderAmount;
    private Integer senderCcy;
    private String senderTrId;
    private Boolean senderSuccess;
    private String senderDate;
    private String senderStatus;
    private String senderStatusText;
    private Long senderBillId;
    private String senderGateway;
    private Long gatewayAmount;
    private String recipientName;
    private String recipientAccount;
    private Long recipientServiceId;
    private Long recipientAmount;
    private Integer recipientCcy;
    private String recipientTrId;
    private Boolean recipientSuccess;
    private String recipientDate;
    private String recipientStatus;
    private String recipientStatusText;
    private Long recipientBillId;
    private String recipientGateway;
    private Integer systemCommissionUpAmount;
    private Integer systemCommissionDownAmount;
    private Integer systemFeeAmount;
    private Integer agentFeeAmount;
    private Integer roundAmount;
    private Integer systemFee;
    private Integer systemCurrency;
    private Integer ccyRate;
    private ResponseStatus status;
    private String comment;
    private String date;
    private String payType;
    private String requestSubAccount;
    private String eposMerchant;
    private String eposTerminal;
    private Boolean main;
    private Integer amountRatio;
    private String senderBean;
    private String category;
    private String humoRefNumber;
    private String humoRefNumber2;

}
