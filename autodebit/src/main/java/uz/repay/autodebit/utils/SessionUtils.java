package uz.repay.autodebit.utils;

import java.util.UUID;

public class SessionUtils {

    public static String generateSessionID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
