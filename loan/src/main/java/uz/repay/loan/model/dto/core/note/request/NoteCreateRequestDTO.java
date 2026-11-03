package uz.repay.loan.model.dto.core.note.request;

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
public class NoteCreateRequestDTO {
    @JsonProperty("loanExtId")
    @LoanId
    @Schema(name = "loanExtId", description = "loan external id in ABS", example = "0098127386", pattern = "\\d{9,}")
    private String loanExtId;
    @JsonProperty("note")
    @NotBlank
    @Schema(name = "note", description = "note to loan", example = "This debtor is always paying late")
    private String note;
}
