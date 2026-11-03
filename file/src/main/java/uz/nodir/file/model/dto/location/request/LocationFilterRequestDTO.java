package uz.nodir.file.model.dto.location.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class LocationFilterRequestDTO extends PageParams {

    @LoanId
    @JsonProperty("loan_ext_id")
    @Schema(
            name = "loan_ext_id",
            example = "01121158076",
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "\\d{9,}",
            description = "loan external id"
    )
    private String loanExtId;
}
