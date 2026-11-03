package uz.repay.payment.model.dto.core.pay.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.AmountType;
import uz.repay.common.model.enums.Currency;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationAmount {

    @JsonProperty("amount")
    @Schema(name = "amount", example = "26000", description = "amount")
    private Long amount;

    @JsonProperty("currency")
    @Schema(name = "currency", example = "UZS", description = "currency")
    private Currency currency;

    @JsonProperty("type")
    @Schema(name = "type", example = "COMMISSION", description = "type of amount")
    private AmountType type;


}
