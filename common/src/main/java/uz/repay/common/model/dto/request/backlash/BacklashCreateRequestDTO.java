package uz.repay.common.model.dto.request.backlash;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BacklashCreateRequestDTO {
    @LoanId
    private String loanId;
    @Min(1)
    private Long spentTime = 0L;
    @NotNull
    private Processing processing;
    @NotNull
    private StatusType type;
}
