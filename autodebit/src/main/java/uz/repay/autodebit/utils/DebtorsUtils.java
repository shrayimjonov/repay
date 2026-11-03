package uz.repay.autodebit.utils;

import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.common.model.dto.response.loan.LoanShortV2ResponseDTO;
import uz.repay.common.model.enums.Branch;

import java.util.*;

public class DebtorsUtils {
    public static Map<Branch, List<DebtLoansResult>> separateListForEachBranch(List<DebtLoansResult> debtLoansList) {
        var map = new HashMap<Branch, List<DebtLoansResult>>();
        Arrays.stream(Branch
                        .values())
                .forEach(
                        b -> map
                                .put(
                                        b, debtLoansList
                                                .stream()
                                                .filter(d -> d.getBranch().equals(b.getCode()))
                                                .toList()
                                )
                );
        return map;
    }

    public static Map<Branch, List<String>> separateListV3ForEachBranch(List<DebtLoansResult> debtLoansList) {
        var map = new HashMap<Branch, List<String>>();
        Arrays.stream(Branch
                        .values())
                .forEach(
                        b -> map
                                .put(
                                        b, debtLoansList
                                                .stream()
                                                .filter(d -> d.getBranch().equals(b.getCode()))
                                                .map(DebtLoansResult::getLoanExtId)
                                                .toList()
                                )
                );
        return map;
    }

    public static <T> List<List<T>> separateList(List<T> list, int separateCount) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += separateCount) {
            parts.add(
                    new ArrayList<>(
                            list.subList(i, Math.min(N, i + separateCount)))
            );
        }
        return parts;
    }

    public static Map<Branch, List<String>> separateListV2ForEachBranch(List<LoanShortV2ResponseDTO> loanInsolvent) {
        var map = new HashMap<Branch, List<String>>();
        Arrays.stream(Branch
                        .values())
                .forEach(
                        b -> map
                                .put(
                                        b,
                                        loanInsolvent
                                                .stream()
                                                .filter(d -> d.getBranch().getCode().equals(b.getCode()))
                                                .map(LoanShortV2ResponseDTO::getExtId)
                                                .toList()
                                )
                );
        return map;
    }
}
