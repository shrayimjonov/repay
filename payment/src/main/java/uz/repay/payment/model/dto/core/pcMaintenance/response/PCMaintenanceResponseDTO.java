package uz.repay.payment.model.dto.core.pcMaintenance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;
import uz.repay.payment.model.entity.PCMaintenance;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCMaintenanceResponseDTO {
    private Long id;
    private Processing processing;


    public PCMaintenanceResponseDTO(PCMaintenance pcMaintenance) {
        this.id = pcMaintenance.getId();
        this.processing = pcMaintenance.getProcessing();
    }

}
