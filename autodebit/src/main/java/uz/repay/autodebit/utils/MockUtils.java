package uz.repay.autodebit.utils;

import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MockUtils {

    public static DebtLoansResult mockOneDebtLoan() {
        return new DebtLoansResult(
                1L,
                "AZIMOV ZAFAR",
                "998901234567",
                "0098127386",
                "00981",
                123L,
                12312L,
                "AA",
                "0569847",
                "3154645674897",
                LocalDateTime.now(),
                LocalDate.now(),
                "system",
                true,
                -1L
        );
    }



}
