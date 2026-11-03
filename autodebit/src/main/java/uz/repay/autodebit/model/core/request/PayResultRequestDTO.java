package uz.repay.autodebit.model.core.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayResultRequestDTO {
    @NotBlank
    @Pattern(regexp = "^(?=.{32}$)[A-Z0-9]*$")
    private String sessionId;
}
