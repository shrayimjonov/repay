package uz.repay.common.model.dto.request.core.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.SimpleDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeParams {
    @SimpleDate
    @Schema(name = "startDate", example = "01.02.2024", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Start date")
    private String startDate;
    @SimpleDate
    @Schema(name = "endDate", example = "29.02.2024", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "End date")
    private String endDate;
}
