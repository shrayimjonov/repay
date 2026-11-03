package uz.repay.loan.model.dto.core.guarantor.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.annotations.Phone;
import uz.repay.common.model.dto.response.annotations.SimpleDate;
import uz.repay.common.model.enums.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuarantorAddRequestDTO {

    @NotBlank
    @Pattern(regexp = "\\d{14}")
    @JsonProperty("person_code")
    @Schema(name = "person_code", description = "PINFL of person", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "30000000000000")
    private String personCode;

    @SimpleDate
    @JsonProperty("birth_date")
    @Schema(name = "birth_date", description = "birthday of person", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "29.08.1996")
    private String birthDate;

    @LoanId
    @JsonProperty("loan_ext_id")
    @Schema(name = "loan_ext_id", description = "loan external id", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "01121158076")
    private String loanExtId;

    @Phone
    @JsonProperty("phone")
    @Schema(name = "phone", description = "phone of guarantor", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "998901234567")
    private String phone;

    @JsonProperty("language")
    @Schema(name = "language", example = "RU", requiredMode = Schema.RequiredMode.REQUIRED, description = "language of loan messages")
    @NotNull
    private Language language;



}
