package uz.repay.card.model.dto.core.response.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.repay.card.model.dto.core.response.reconciliation.result.ReconciliationResult;
import uz.repay.card.model.entity.Reconciliation;
import uz.repay.common.model.dto.response.core.result.PageResult;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReconciliationFilterResponseDTO extends PageResult {
    private List<ReconciliationResult> list;

    public ReconciliationFilterResponseDTO(Page<Reconciliation> pageResult) {
        super(
                pageResult.getSize(),
                pageResult.getNumber() + 1,
                pageResult.getNumberOfElements(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.isLast(),
                pageResult.isFirst()
        );

        this.list = pageResult.getContent()
                .stream()
                .map(ReconciliationResult::new)
                .toList();
    }
}
