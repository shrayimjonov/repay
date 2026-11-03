package uz.repay.loan.model.dto.core.debtLoan.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DebtLoansRequestDTO extends PageParams {
    @Schema(name = "DebtLoanFilterParams", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Find debtLoan by params")
    @JsonProperty("DebtLoanFilterParams")
    private AllDebtLoanParams params;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllDebtLoanParams {
        @Schema(name = "debtLoanName", example = "AYIBJONOV SHOHRUX KAMOLJON OGLI", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Client name")
        @JsonProperty("debtLoanName")
        private String name;


        @Schema(name = "debtLoanPhone", example = "998901234567", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Client phone number")
        @JsonProperty("debtLoanPhone")
        private String phone;


        @Schema(name = "debtLoanExt", example = "01121122507", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Client loan external id in core banking")
        @JsonProperty("debtLoanExt")
        private String loanExtId;


        @Schema(name = "debtLoanPINFL", example = "30000000000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Client person code")
        @JsonProperty("debtLoanPINFL")
        private String personCode;
    }
}
