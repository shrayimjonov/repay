package uz.repay.payment.service.business;

import uz.repay.payment.model.entity.Transaction;

public interface ReverseService {
    void cancelPC(String utrnno,
                  String merchantId,
                  String terminalId,
                  String account,
                  Transaction transaction
    );
    @Deprecated
    void reverseABS(Transaction transaction, String merchantId, String terminalId, String creditTo);

    void reverseForexOperation(Transaction transaction);

    void reverseOnlyOneABSPaymentV2(Transaction transaction, String creditTo);

    void reverseTransitAndForex(Transaction transaction);
}
