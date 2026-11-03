package uz.repay.loan.model.dto.core.loan.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;
import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoanSearchRequestDTO extends PageParams {
    @Schema(name = "params", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Filter loans by params")
    private LoanSearchParams params;

}
