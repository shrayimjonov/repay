package uz.repay.common.utils;

import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.model.enums.Branch;

import java.util.Arrays;

public class BranchUtils {

    public static String branchRegexString() {
        return "(00981)|(01046)|(01069)|(01072)|(01086)|(01121)|(01122)";
    }

    public static Branch getBranchByLoan(String loanExtId) {
        return
                Arrays.stream(Branch
                                .values())
                        .filter(b -> b.getCode().equals(loanExtId.substring(0, 5)))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Branch not found for loan: %s".formatted(loanExtId)));
    }
}
