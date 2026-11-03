package uz.repay.report.model.dto.report.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanGraphRequestDTO {
    @LoanId
    @JsonProperty("loan_ext_id")
    @Schema(name = "loan_ext_id", description = "loan external id", requiredMode = Schema.RequiredMode.REQUIRED, example = "01121158076")
    private String loanExtId;
    @NotBlank
    @JsonProperty("client_name")
    @Schema(name = "client_name", description = "client full name", requiredMode = Schema.RequiredMode.REQUIRED, example = "SIROJIDDINOV NODIRBEK")
    private String clientName;
    @NotBlank
    @JsonProperty("transit_account")
    @Schema(name = "transit_account", description = "loan transit account", requiredMode = Schema.RequiredMode.REQUIRED, example = "29896024548948464001")
    private String transitAccount;
    @NotNull
    @JsonProperty("loan_amount")
    @Schema(name = "loan_amount", description = "taken amount", requiredMode = Schema.RequiredMode.REQUIRED, example = "12412412")
    private Long loanAmount;
    @NotNull
    @JsonProperty("recommended_amount")
    @Schema(name = "recommended_amount", description = "this month principal debt amount", requiredMode = Schema.RequiredMode.REQUIRED, example = "976546")
    private Long recommendedAmount;
    @NotNull
    @JsonProperty("max_amount")
    @Schema(name = "max_amount", description = "maximal amount for repay debt", requiredMode = Schema.RequiredMode.REQUIRED, example = "645234213")
    private Long maxAmount;
    @NotNull
    @JsonProperty("loan_open_date")
    @Schema(name = "loan_open_date", description = "loan issued date", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-12-12")
    private LocalDate loanOpenDate;


}
