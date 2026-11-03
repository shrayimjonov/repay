package uz.repay.payment.utils;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.enums.AmountType;
import uz.repay.common.model.enums.Currency;
import uz.repay.payment.model.dto.core.pay.response.result.OperationAmount;
import uz.repay.payment.model.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PaymentUtils {

    public static final String SWITCHER_REVERSE = "REVERSE";

    public static final String MASTER_DEFAULT_PAY_TYPE = "REPAY";
    public static final String MASTER_DEFAULT_PAY_OPERATION = "DEBIT";

    public static List<OperationAmount> buildTransactionAmount(Transaction transaction) {
        List<OperationAmount> amounts = new ArrayList<>();
        log.debug("Transaction currency: {}", transaction.getCurrency());

        if (!transaction.getCurrency().equals(Currency.UZS)) {

            if (transaction.getRate() != null) {
                OperationAmount operationAmount = new OperationAmount(
                        transaction.getAmount() * transaction.getRate(),
                        Currency.UZS,
                        AmountType.CONVERTED
                );

                log.info("Prepared operation amount: {}", operationAmount);
                amounts.add(
                        operationAmount
                );
            }
        }

        return amounts;
    }
}
