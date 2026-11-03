package uz.repay.card.model.dto.backlash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BacklashEventDTO {
    private Long spentTime;
    private Processing processing;
}
