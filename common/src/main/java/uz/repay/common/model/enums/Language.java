package uz.repay.common.model.enums;

import lombok.Getter;

@Getter
public enum Language {
    EN,
    RU,
    UZ,
    NONE;

    public static Language defaultLang() {
        return Language.NONE;
    }
}
