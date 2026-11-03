package uz.repay.payment.model.entity.meta;


public final class TransactionFields {

    public static final String ID = "id";
    public static final String BRANCH = Transaction_.BRANCH;
    public static final String CARD_OWNER = "cardOwner";
    public static final String STATUS = Transaction_.STATUS;
    public static final String REF_NUM = Transaction_.REF_NUM;
    public static final String NOTE = "note";
    public static final String AMOUNT = Transaction_.AMOUNT;
    public static final String REVERSE_REASON = "reverseReason";
    public static final String LOAN_EXT_ID = Transaction_.LOAN_EXT_ID;
    public static final String CARD_PAN = Transaction_.CARD_PAN;
    public static final String PAY_TYPE = "payType";
    public static final String TERMINAL_ID = "terminalId";
    public static final String CURRENCY = "currency";
    public static final String INIT_TIME = "initTime";
    public static final String CREATED_DATE = Transaction_.CREATED_DATE;
    public static final String CREATED_BY = "createdBy";

    private TransactionFields() {
    }
}
