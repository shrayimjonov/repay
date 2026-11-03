package uz.repay.common.model.dto.request.payment.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationParams {
    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "phone to sending message")
    private String phone;

    @NotBlank
    @JsonProperty("fullName")
    @Schema(name = "fullName", example = "ESHMATOV TOSHMAT", requiredMode = Schema.RequiredMode.REQUIRED, description = "Full name of card holder")
    private String fullName;


    @NotNull
    @Min(1)
    @JsonProperty("loanId")
    @Schema(name = "loanId", example = "6412", requiredMode = Schema.RequiredMode.REQUIRED, description = "loan id")
    private Long loanId = 0L;

    @JsonProperty("language")
    @Schema(name = "language", example = "UZ", requiredMode = Schema.RequiredMode.REQUIRED, description = "language for message")
    private Language language = Language.UZ;

}
