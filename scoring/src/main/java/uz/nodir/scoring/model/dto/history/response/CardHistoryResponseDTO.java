package uz.nodir.scoring.model.dto.history.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.scoring.model.dto.monitoring.response.result.MonitoringResult;
import uz.nodir.scoring.model.enums.monitoring.MonitoringOperation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardHistoryResponseDTO {
    @JsonProperty("card")
    @Schema(name = "card", example = "561468******7308", description = "masked pan of card")
    private String card;
    @JsonProperty("token")
    @Schema(name = "token", example = "3BE40C1C1C344B85BFDA9283F2C96B3D", description = "card token")
    private String token;
    @JsonProperty("payment_id")
    @Schema(name = "payment_id", example = "022354773922", description = "transaction id in processing")
    private String paymentId;
    @JsonProperty("trans_type")
    @Schema(name = "trans_type", example = "Оплата в обычном терминале", description = "description of transaction")
    private String transType;
    @JsonProperty("created_date")
    @Schema(name = "created_date", example = "2024-10-02 18:14:24", description = "date and time of transaction")
    private String createdDateTime;
    @JsonProperty("merchant")
    @Schema(name = "merchant", example = "90510014676", description = "ePos merchant")
    private String merchant;
    @JsonProperty("terminal")
    @Schema(name = "terminal", example = "19041", description = "ePos terminal")
    private String terminal;
    @JsonProperty("tran_type")
    @Schema(name = "tran_type", example = "DEBIT", description = "tran type of transaction")
    private MonitoringOperation creditOrDebit;
    @JsonProperty("amount")
    @Schema(name = "amount", example = "40000", description = "operation amount")
    private Long amount;
    @JsonProperty("full_amount")
    @Schema(name = "full_amount", example = "40500", description = "operation amount with commission")
    private Long amountWithCommission;
    @JsonProperty("merchant_name")
    @Schema(name = "merchant_name", example = "OOO KARAVAN SBS", description = "merchant name")
    private String merchantName;
    @JsonProperty("address")
    @Schema(name = "address", example = "Tashkent Toshkent shahar Uctepa tumani U", description = "merchant address")
    private String address;
    @JsonProperty("reversal")
    @Schema(name = "reversal", example = "false", description = "transaction is reversed or not")
    private boolean reversal;
    @JsonProperty("currency")
    @Schema(name = "currency", example = "860", description = "currency of amount")
    private String currency;

    public CardHistoryResponseDTO(MonitoringResult result) {
        this.card = result.getCard();
        this.token = result.getToken();
        this.paymentId = result.getPaymentId();
        this.transType = result.getTransType();
     //   this.createdTime = result.getCreatedTime();
        this.createdDateTime = result.getCreatedDate().toString() + " " + result.getCreatedTime().toString();
        this.merchant = result.getMerchant();
        this.terminal = result.getTerminal();
        this.creditOrDebit = result.getCreditOrDebit();
        this.amount = result.getAmount();
        this.amountWithCommission = result.getAmountWithCommission();
        this.merchantName = result.getMerchantName();
        this.address = result.getAddress();
        this.reversal = result.isReversal();
        this.currency = result.getCurrency();
    }

    public CardHistoryResponseDTO(List<MonitoringResult> monitoringResults) {
    }
}
