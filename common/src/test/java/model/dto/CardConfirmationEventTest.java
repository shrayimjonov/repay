package model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.event.CardConfirmationEvent;

public class CardConfirmationEventTest {
    private final static String CODE = "486954";
    private final static String PAN = "561468******7308";
    private final static String LOAN_EXT_ID = "01121158076";
    private final static String PHONE = "998901234567";
    private final static Language LANGUAGE = Language.defaultLang();

    @Test
    public void check(){

        var valuesString = new String[]
                {
                        LOAN_EXT_ID,
                        PHONE,
                        PAN
                };

       var eventDTO = new CardConfirmationEvent(
                CODE,
                PAN,
                LOAN_EXT_ID,
                PHONE,
                LANGUAGE.name()
        );

        Assertions.assertArrayEquals(valuesString, eventDTO.fieldValues());
    }
}
