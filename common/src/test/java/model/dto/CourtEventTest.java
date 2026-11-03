package model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.event.CourtEvent;



public class CourtEventTest {
    private final static String MESSAGE = "Test Message";
    private final static String LOAN_EXT_ID = "01121158076";
    private final static String PHONE = "998901234567";
    private final static Language LANGUAGE = Language.defaultLang();

    @Test
    public void check() {

        var valuesString = new String[]
                {
                        MESSAGE,
                        LOAN_EXT_ID,
                        PHONE,
                        LANGUAGE.name(),

                };
        var eventDTO = new CourtEvent(
                MESSAGE,
                LOAN_EXT_ID,
                PHONE,
                LANGUAGE.name()
        );

        Assertions.assertArrayEquals(valuesString, eventDTO.fieldValues());
    }
}
