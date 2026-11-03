package uz.repay.payment.model.dto.core.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Branch;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchResponseDTO {
    private Branch branch;
    private String code;
    private String name;

    public BranchResponseDTO(Branch branch) {
        this.branch = branch;
        this.code = branch.getCode();
        this.name = branch.getName();
    }
}
