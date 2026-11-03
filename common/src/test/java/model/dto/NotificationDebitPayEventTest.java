package model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.event.NotificationDebitPayEvent;

public class NotificationDebitPayEventTest {

    private final static String PAN = "561468******7308";
    private final static String LOAN_EXT_ID = "01121158076";
    private final static String PHONE = "998901234567";
    private final static String FULL_NAME = "ESHMATOV TOSHMAT";
    private final static Long AMOUNT = 5_111L;
    private final static Language LANGUAGE = Language.defaultLang();
    private final static Payer PAYER = Payer.AUTO;

    @Test
    public void check(){
        var valuesString = new String[]
                {
                        LOAN_EXT_ID,
                        AMOUNT.toString(),
                        PAN,
                        PHONE,
                };

        var eventDTO = new NotificationDebitPayEvent(
               FULL_NAME,
                AMOUNT,
                PAYER.name(),
                PAN,
                LOAN_EXT_ID,
                PHONE,
                LANGUAGE.name()
        );

        Assertions.assertArrayEquals(valuesString, eventDTO.fieldValues());
    }
}
