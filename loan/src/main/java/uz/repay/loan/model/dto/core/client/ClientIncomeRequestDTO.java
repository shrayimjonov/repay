package uz.repay.loan.model.dto.core.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientIncomeRequestDTO {
    @NotBlank
    @Pattern(regexp = "^\\d{14}$")
    @Schema(name = "personCode", example = "30000000000000", requiredMode = Schema.RequiredMode.REQUIRED, description = "PINFL", pattern = "^\\d{14}$")
    private String personCode;
}
