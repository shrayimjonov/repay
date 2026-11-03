package uz.repay.payment.service.business;

public interface NotificationService {

    void sendMessageAfterDebit(String phone, String fullName, String loanExtId, Long amount, String pan);
}
