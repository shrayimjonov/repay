package uz.repay.card.model.dto.core.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequestDTO {
    @NotBlank
    @JsonProperty("id")
    @Schema(name = "id", example = "61", requiredMode = Schema.RequiredMode.REQUIRED, description = "id code from verify response body")
    private String id;

    @NotBlank
    @JsonProperty("code")
    @Schema(name = "code", example = "487985", requiredMode = Schema.RequiredMode.REQUIRED, description = "sent code to holder phone")
    private String code;

    @NotNull
    @JsonProperty("type")
    @Schema(name = "type", example = "HUMO", requiredMode = Schema.RequiredMode.REQUIRED, description = "type from verify response")
    private Processing type;

    @NotNull
    @JsonProperty("payer")
    @Schema(name = "payer", example = "BORROWER", requiredMode = Schema.RequiredMode.REQUIRED, description = "payer type")
    private Payer payer;
}
