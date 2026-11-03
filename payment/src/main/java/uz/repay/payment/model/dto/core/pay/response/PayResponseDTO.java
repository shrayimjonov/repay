package uz.repay.payment.model.dto.core.pay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.dto.core.pay.response.result.OperationAmount;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.utils.PaymentUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayResponseDTO {

    @JsonProperty("id")
    @Schema(name = "id", example = "55", description = "id of transaction")
    private Long id;

    @JsonProperty("branch")
    @Schema(name = "branch", example = "_01121", description = "branch")
    private Branch branch;

    @JsonProperty("cardOwner")
    @Schema(name = "cardOwner", example = "TOSHMATOV AZAMAT", description = "card holder")
    private String cardOwner;

    @JsonProperty("status")
    @Schema(name = "status", example = "SENDER_SUCCESS", description = "transaction status")
    private ResponseStatus status;

    @JsonProperty("refNum")
    @Schema(name = "refNum", example = "052225588", description = "payment id in processing")
    private String refNum;

    @JsonProperty("note")
    @Schema(name = "note", example = "Some text", description = "Error reason text")
    private String note;

    @JsonProperty("amount")
    @Schema(name = "amount", example = "26000", description = "amount")
    private Long amount;

    @JsonProperty("reverseReason")
    @Schema(name = "reverseReason", example = "2 marta tolov qilgan", description = "reverse reason")
    private String reverseReason;

    @JsonProperty("loanExtId")
    @Schema(name = "loanExtId", example = "01121158076", description = "loan external id")
    private String loanExtId;

    @JsonProperty("cardPan")
    @Schema(name = "cardPan", example = "561468******9987", description = "masked pan number")
    private String cardPan;

    @JsonProperty("payType")
    @Schema(name = "payType", example = "MANUAL", description = "debited type")
    private AddType payType;

    @JsonProperty("initTime")
    @Schema(name = "initTime", example = "2024-02-05 17:17:03.935", description = "debited type")
    private LocalDateTime initTime;

    @JsonProperty("createdBy")
    @Schema(name = "createdBy", example = "user@example.com", description = "debited by")
    private String createdBy;

    @JsonProperty("terminalId")
    @Schema(name = "terminalId", example = "92902598", description = "terminal")
    private String terminalId;

    @JsonProperty("currency")
    @Schema(name = "currency", example = "UZS", description = "currency")
    private Currency currency;

    @JsonProperty("amounts")
    @Schema(name = "amounts", type="array", example = """
 [
                   {
                     "amount" : 555,
                     "currency" : "USD",
                     "type" : "TOTAL"
                   },
                      {
                     "amount" : 21,
                     "currency" : "USD",
                     "type" : "COMMISSION"
                   },
                      {
                     "amount" : 12988446,
                     "currency" : "UZS",
                     "type" : "CONVERTED"
                   }
                ]""", description = "Transaction amounts (commission, total, converted)")
    private List<OperationAmount> amounts = new ArrayList<>();


    public PayResponseDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.branch = transaction.getBranch();
        this.cardOwner = transaction.getCardOwner();
        this.status = transaction.getStatus();
        this.refNum = transaction.getRefNum();
        this.note = transaction.getNote();
        this.amount = transaction.getAmount();
        this.reverseReason = transaction.getReverseReason();
        this.loanExtId = transaction.getLoanExtId();
        this.cardPan = transaction.getCardPan();
        this.payType = transaction.getPayType();
        this.initTime = transaction.getInitTime();
        this.createdBy = transaction.getCreatedBy();
        this.terminalId = transaction.getTerminalId();
        this.currency = transaction.getCurrency();
        this.amounts = PaymentUtils.buildTransactionAmount(transaction);
    }
}
