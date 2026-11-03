package uz.repay.card.model.dto.core.request.reco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.DateRange;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationFilterParams {
    private String terminalId;
    private DateRange range;
    private List<ResponseStatus> statuses;
    private String paymentId;

}
