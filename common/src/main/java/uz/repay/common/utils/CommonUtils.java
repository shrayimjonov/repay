package uz.repay.common.utils;

import jakarta.annotation.Nullable;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    public static String phoneRegex() {
        return "^998\\d{9}$";
    }

    public static String removeNonUtf8CompliantCharacters(final String s) {
        StringBuilder alphanumeric = new StringBuilder();
        char[] charArray = s.toCharArray();
        int i = 0;
        while (i < charArray.length) {
            if (Character.isLetterOrDigit(charArray[i]) || Character.isSpaceChar(charArray[i])
                    || Character.isWhitespace(charArray[i])) {
                alphanumeric.append(charArray[i]);
            }
            i++;
        }
        return alphanumeric.toString();

    }

    public static boolean validUTF8(byte[] input) {
        int i = 0;
        // Check for BOM
        if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
                && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
            i = 3;
        }

        int end;
        for (int j = input.length; i < j; ++i) {
            int octet = input[i];
            if ((octet & 0x80) == 0) {
                continue; // ASCII
            }

            // Check for UTF-8 leading byte
            if ((octet & 0xE0) == 0xC0) {
                end = i + 1;
            } else if ((octet & 0xF0) == 0xE0) {
                end = i + 2;
            } else if ((octet & 0xF8) == 0xF0) {
                end = i + 3;
            } else {
                // Java only supports BMP so 3 is max
                return false;
            }

            while (i < end) {
                i++;
                octet = input[i];
                if ((octet & 0xC0) != 0x80) {
                    // Not a valid trailing byte
                    return false;
                }
            }
        }
        return true;
    }

    public static String handleAmount(Long amount) {
        return NumberFormat.getInstance().format(amount / 100.0);
    }

    public static String phoneImprover(String phone) {
        if (phone.startsWith("+"))
            phone = phone.replace("+", "");

        if (phone.length() == 12 && phone.startsWith("000"))
            phone = phone.replace("000", "998");

        if (phone.length() == 12 && phone.startsWith("00"))
            phone = phone.replace("00", "99");

        if (phone.matches("\\d{9}"))
            phone = "998" + phone;

        return phone;
    }

    @Nullable
    public static Object getNested(Map<Object, Object> map, Object... keys) {
        if (map == null || keys == null || keys.length == 0) {
            return null;
        }

        Object current = map;

        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];

            if (!(current instanceof Map)) {
                return null;
            }

            Map<?, ?> currentMap = (Map<?, ?>) current;
            Object value = currentMap.get(key);

            if (value == null) {
                return null;
            }

            if (i == keys.length - 1) {
                return value; // <- Ключ последний, возвращаем
            }

            current = value;
        }

        return null;
    }
}
