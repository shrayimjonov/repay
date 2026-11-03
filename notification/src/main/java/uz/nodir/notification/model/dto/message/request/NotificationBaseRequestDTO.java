package uz.nodir.notification.model.dto.message.request;

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
public class NotificationBaseRequestDTO {
    @LoanId
    @JsonProperty("loan_ext_id")
    @Schema(name = "loan_ext_id", example = "0098127386", requiredMode = Schema.RequiredMode.REQUIRED, description = "loan external id")
    private String loanExtId;

    @Phone
    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", requiredMode = Schema.RequiredMode.REQUIRED, description = "phone number")
    private String phone;

    @NotNull
    @JsonProperty("language")
    @Schema(name = "language", example = "RU", description = "message language")
    private Language language;


}
