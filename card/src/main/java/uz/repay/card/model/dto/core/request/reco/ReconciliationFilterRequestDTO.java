package uz.repay.card.model.dto.core.request.reco;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReconciliationFilterRequestDTO extends PageParams {
    @Valid
    private ReconciliationFilterParams params;
}
