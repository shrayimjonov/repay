package uz.repay.payment.model.dto.core.pcMaintenance.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCMaintenanceRequestDTO {
    @NotNull
    private Processing processing;
}
