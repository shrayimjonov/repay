package uz.repay.common.model.dto.request.loan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanSimpleRequestDTO {
    @LoanId
    @Schema(name = "loanExtId", example = "0098127386", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "\\d{9,}")
    private String loanExtId;

    public Map<Object, Object> toMap() {
        return Map.of("loanExtId", this.loanExtId);
    }
}
