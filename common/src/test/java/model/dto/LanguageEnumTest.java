package model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uz.repay.common.model.enums.Language;

public class LanguageEnumTest {

    @Test
    public void checkDefaultLanguage() {
        var defaultLanguage = Language.NONE;

        Assertions.assertEquals(defaultLanguage, Language.defaultLang());
    }
}
