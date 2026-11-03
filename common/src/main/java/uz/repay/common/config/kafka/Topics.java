package uz.repay.common.config.kafka;

public class Topics {
    public final static String PAY_NOTIFICATION = "payNotification";
    public final static String SMS_DEBT = "debtSMS";
    public final static String SMS_DEBIT_PAY = "debitPaySMS";
    public final static String CARD_CONFIRMATION = "cardConfirmation";
    public final static String LOG_PROTOCOL = "logProtocol";
    public final static String NOTIFICATION_CARD_ATTACHED = "cardAttached";
    public final static String NOTIFICATION_COURT_ALERT = "notificationCourtAlert";
    public final static String LOG_TO_SHOW_EXCEPTION = "logExceptionShow";
    public final static String CARD_DEBEZIUM_NEW_LOAN_CARD_FINDER = "repay.public.new_loan";

    public final static String PAYMENT_TRANSACTION_TOPIC = "PAYMENT_TRANSACTION";
    public final static String PAYMENT_TRANSACTION_SMS_NOTIFICATION_STREAMS = "PAYMENT_TRANSACTION_SMS_NOTIFICATION_STREAM";
    public final static String PAYMENT_TRANSACTION_PUSH_NOTIFICATION_STREAMS = "PAYMENT_TRANSACTION_PUSH_NOTIFICATION_STREAM";
    public final static String CARD_CREDIT_EVENT_TOPIC = "CARD_CREDIT_EVENT";

    public final static String CARD_DEACTIVATE_TOPIC = "CARD_DEACTIVATE";
    public final static String CARD_ADD_NOTIFICATION_STREAM = "CARD_ADD_TO_LOAN_NOTIFICATION_STREAM";

    public final static String TELEGRAM_ALERT_NOTIFICATION_TOPIC = "TELEGRAM_ALERT_NOTIFICATION_TOPIC";
}
