package uz.repay.card.model.dto.core.request.epos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EPosCreateRequestDTO {
    @NotNull
    private Branch branch;
    @NotNull
    private Processing processing;
    @NotNull
    private ServiceType service;
    @NotBlank
    private String terminalId;
    @NotBlank
    private String merchantId;
    @NotBlank
    @Pattern(regexp = "\\d{20}")
    private String account;
}
