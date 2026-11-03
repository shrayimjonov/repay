package uz.repay.payment.model.dto.core.transaction.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionFilterRequestDTO extends PageParams {
    @Valid
    private TransactionParams params;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionParams {
        private Long amount;
        @NotEmpty
        private List<Branch> branches;
        private String cardPan;
        private String loanExtId;
        private String refNum;
        private DateRange range;
        @NotEmpty
        private List<ResponseStatus> statuses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        private String dateFrom;
        private String dateTo;
    }
}
