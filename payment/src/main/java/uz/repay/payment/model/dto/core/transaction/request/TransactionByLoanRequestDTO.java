package uz.repay.payment.model.dto.core.transaction.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;
import uz.repay.common.model.dto.response.annotations.LoanId;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionByLoanRequestDTO extends PageParams {
    @Valid
    private TransactionByLoanParams params;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionByLoanParams {
        @LoanId
        private String loanExtId;
    }


}
