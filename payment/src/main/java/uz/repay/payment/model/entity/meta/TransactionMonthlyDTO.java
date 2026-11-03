package uz.repay.payment.model.entity.meta;

public interface TransactionMonthlyDTO {
    String getCreated_date();
    Integer getCount();
    Long getAmount();
    String getBranch();
    String getPay_type();
}
