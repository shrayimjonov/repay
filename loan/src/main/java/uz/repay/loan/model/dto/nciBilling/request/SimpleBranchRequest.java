package uz.repay.loan.model.dto.nciBilling.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.Branch;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleBranchRequest {
    @Branch
    private String branch;
}
