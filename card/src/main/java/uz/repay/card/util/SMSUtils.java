package uz.repay.card.util;

import java.util.Random;

public class SMSUtils {

    public static String getRandomCode() {
        return String.valueOf(new Random().nextInt(100000, 999999));
    }

    public static String generateOTPMessage(String code) {
        return "Никому не сообщайте код! Одноразовый код для добавление карты: %s".formatted(code);
    }

    public static String generateOTPV2(String code, String card, String loanExt) {
        return "SOOBSHITE KOD TOLKA SOTRUDNIKAM BANKA V OFISAX DAVR BANKA! Kod: %s dlya dobavleniya v sistemu kredit pogashenie repay na anketu: %s dlya podklyucheniya karti **** %s."
                .formatted(code, loanExt, card);
    }


}
