package uz.repay.payment.model.dto.core.backlash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BacklashEventDTO {
    private String loanExtId;
    private Long spentTime;
    private Processing processing;
    private StatusType type;
}
