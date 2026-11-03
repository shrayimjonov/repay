package uz.repay.card.util;

import uz.repay.card.model.enums.LoanType;

public class LoanUtils {

    public static LoanType convert(String loanType) {
        switch (loanType) {
            case "Автокредит" -> {
                return LoanType.AUTO_CREDIT;
            }
            case "Кредитная карта", "Потребительский кредит" -> {
                return LoanType.CONSUMER;
            }
            case "Ипотечный кредит" -> {
                return LoanType.MORTGAGE;
            }
            case "Образовательный кредит" -> {
                return LoanType.EDUCATION;
            }
            default -> {
                return LoanType.MICROLOAN;
            }
        }
    }
}
