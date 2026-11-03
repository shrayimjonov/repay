package uz.repay.loan.model.dto.core.loan.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanReportRequestDTO {

    @Schema(name = "params", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "loan filter fields params")
    private LoanSearchParams params;

}
