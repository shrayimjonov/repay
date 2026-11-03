package uz.repay.common.utils;

public class TextUtils {

    @Deprecated
    public static String cyrillicToLatin(String message) {

        char[] abcCyr = russianLetters();

        StringBuilder builder = unicodeTranslatorWithSymbols(message, abcCyr);
        return builder.toString();
    }

    private static char[] russianLetters() {
        return new char[]{' ', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ў', 'ў', 'Қ', 'қ', 'Ғ', 'ғ', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    }

    private static String[] latinLetters() {
        return new String[]{" ", "a", "b", "v", "g", "d", "e", "e", "j", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sh", "", "i", "", "e", "yu", "ya", "A", "B", "V", "G", "D", "E", "E", "J", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh", "Sh", "", "I", "", "E", "Yu", "Ya", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "O", "o", "Q", "q", "G", "g", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    }

    private static StringBuilder unicodeTranslatorWithSymbols(String message, char[] abcCyr) {
        String[] abcLat = latinLetters();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            boolean found = false;
            for (int x = 0; x < abcCyr.length; x++) {
                if (message.charAt(i) == abcCyr[x]) {
                    found = true;
                    builder.append(abcLat[x]);
                    break;
                }
            }


            if (!found) {
                builder.append(message.charAt(i));
            }
        }

        return builder;
    }

    private static StringBuilder unicodeTranslator(String message, char[] abcCyr) {
        String[] abcLat = latinLetters();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < message.length(); i++)
            for (int x = 0; x < abcCyr.length; x++)
                if (message.charAt(i) == abcCyr[x])
                    builder.append(abcLat[x]);


        return builder;
    }

    public static String cyrillicToLatin(String message, boolean leaveSymbols) {

        var abcCyr = russianLetters();

        StringBuilder builder;

        if (leaveSymbols) // simvollarni qoldirish textda: САЛОМ!! -> SALOM!!
            builder = unicodeTranslatorWithSymbols(message, abcCyr);
        else // simvollar o'chirib tashlanadi: САЛОМ!! -> SALOM
            builder = unicodeTranslator(message, abcCyr);

        return builder.toString();
    }
}
