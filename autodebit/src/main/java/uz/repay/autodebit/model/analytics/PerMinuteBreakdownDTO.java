package uz.repay.autodebit.model.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerMinuteBreakdownDTO {
    private String minute;
    private long count;
}
