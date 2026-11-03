package uz.repay.report.model.dto.report.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;
import uz.repay.report.model.enums.ReportType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDTO {
    @NotNull
    @Schema(name = "type", example = "LOAN", requiredMode = Schema.RequiredMode.REQUIRED, description = "Set report type and get excel")
    private ReportType type;
    @Schema(name = "params", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Set params like created date or something else")
    private LoanSearchParams params;

}
