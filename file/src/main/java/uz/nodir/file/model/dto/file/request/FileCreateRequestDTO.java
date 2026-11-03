package uz.nodir.file.model.dto.file.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCreateRequestDTO {
    @LoanId
    @JsonProperty("loanExtId")
    @Schema(
            name = "loanExtId",
            example = "01121158076",
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "\\d{9,}",
            description = "loan external id"
    )
    private String loanExtId;
    @NotBlank
    @JsonProperty("description")
    @Schema(
            name = "description",
            example = "Passport rasmi",
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "description of file"
    )
    private String description;
}
