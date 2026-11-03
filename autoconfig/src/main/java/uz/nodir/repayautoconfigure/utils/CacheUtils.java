package uz.nodir.repayautoconfigure.utils;

import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.List;

public class CacheUtils {
    public static final String TC_STATUS = "card_status";
    public static final String LOAN_CARD = "card_loan_card";
    public static final String LOAN = "loan";
    public static final String DEBT_LOAN = "loan_debtLoan";
    public static final String TERMINAL = "card_terminal";
    public static final String ABS_SETTINGS = "abs_settings";
    public static final String SMS_TEMPLATE = "sms_template";


    public static List<String> getCacheNames() {
        return List.of(TC_STATUS, LOAN, LOAN_CARD,
                DEBT_LOAN, TERMINAL, ABS_SETTINGS,
                SMS_TEMPLATE);
    }

    public static List<ConcurrentMapCache> getAllCaches() {
        return List.of(
                new ConcurrentMapCache(TC_STATUS),
                new ConcurrentMapCache(LOAN_CARD),
                new ConcurrentMapCache(LOAN),
                new ConcurrentMapCache(DEBT_LOAN),
                new ConcurrentMapCache(TERMINAL),
                new ConcurrentMapCache(ABS_SETTINGS),
                new ConcurrentMapCache(SMS_TEMPLATE)
        );
    }
}
