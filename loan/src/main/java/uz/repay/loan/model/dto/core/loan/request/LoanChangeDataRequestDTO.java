package uz.repay.loan.model.dto.core.loan.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.annotations.Phone;
import uz.repay.common.model.enums.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanChangeDataRequestDTO {
    @LoanId
    @JsonProperty("loanExt")
    @Schema(name = "loanExt", example = "01121158076", requiredMode = Schema.RequiredMode.REQUIRED, description = "Loan external id")
    private String loanExtId;

    @Phone(message = "Phone regex only 12 digits!")
    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", requiredMode = Schema.RequiredMode.REQUIRED, description = "phone number")
    private String phone;

    @JsonProperty("language")
    @Schema(name = "language", example = "RU", requiredMode = Schema.RequiredMode.REQUIRED, description = "language of loan messages")
    @NotNull
    private Language language;


}
