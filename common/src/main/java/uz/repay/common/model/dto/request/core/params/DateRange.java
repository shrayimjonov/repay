package uz.repay.common.model.dto.request.core.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {
    private String dateFrom;
    private String dateTo;
}
