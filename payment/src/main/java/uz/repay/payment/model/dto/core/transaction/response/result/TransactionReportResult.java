package uz.repay.payment.model.dto.core.transaction.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.AddType;
import uz.repay.payment.model.entity.domain.MonthlyReportDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportResult {
    private AddType payType;
    private Long amount;
    private Integer count;

    public TransactionReportResult(MonthlyReportDTO t) {
        this.payType = t.getPay_type();
        this.amount = t.getAmount();
        this.count = t.getCount();
    }
}
