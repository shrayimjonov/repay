package uz.repay.autodebit.model.result.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.autodebit.model.result.request.params.LoanResultParams;
import uz.repay.common.model.dto.request.core.params.PageParams;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoanResultRequestDTO extends PageParams {
    @Valid
    private LoanResultParams params;
}
