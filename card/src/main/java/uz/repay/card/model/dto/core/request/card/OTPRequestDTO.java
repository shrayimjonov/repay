package uz.repay.card.model.dto.core.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.Card;
import uz.repay.common.model.dto.response.annotations.Phone;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequestDTO {
    @Card(message = "Card must be consist of 16 digits")
    @JsonProperty("card")
    @Schema(name = "card", example = "9860250000000000", description = "Card number", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "\\d{16}")
    private String card;

    @NotBlank
    @Pattern(regexp = "\\d{4}")
    @JsonProperty("expiry")
    @Schema(name = "expiry", example = "2703", description = "expiry date of card", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "\\d{4}")
    private String expiry;

    @Min(1)
    @NotNull
    @JsonProperty("loanId")
    @Schema(name = "loanId", example = "16", requiredMode = Schema.RequiredMode.REQUIRED, description = "Loan id")
    private Long loanId = 0L;

    @Phone
    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", requiredMode = Schema.RequiredMode.REQUIRED, description = "card attached phone number")
    private String phone;


}
