package uz.repay.autodebit.model.result.request.params;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanResultParams {
    @NotBlank
    private String loanExtId;
}
