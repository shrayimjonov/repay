package uz.repay.common.utils;

import org.bouncycastle.util.encoders.Base64;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.model.enums.Processing;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class CardUtils {

    public final static String cryptKey = "DbvR45$1086#kjNS";
    public final static String cipherType = "AES/CBC/PKCS5PADDING";


    public static String maskPanDigits(String pan) {
        return pan.substring(0, 6) + "******" + pan.substring(12, 16);
    }

    public static String decryptPan(String pan) {
        try {
            Cipher c = Cipher.getInstance(cipherType);
            c.init(Cipher.DECRYPT_MODE, AESkey(cryptKey), new IvParameterSpec(cryptKey.getBytes(StandardCharsets.UTF_8)));
            byte[] byteStr = Base64.decode(pan.getBytes());
            return new String(c.doFinal(byteStr), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    public static String decryptBulkPan(String pan) {
        try {
            Cipher c = Cipher.getInstance(cipherType);
            c.init(Cipher.DECRYPT_MODE, AESkey(cryptKey), new IvParameterSpec(cryptKey.getBytes(StandardCharsets.UTF_8)));
            byte[] byteStr = Base64.decode(pan.getBytes());
            return new String(c.update(byteStr), StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    public static String encryptPan(String pan) {
        try {
            Cipher c = Cipher.getInstance(cipherType);

            c.init(Cipher.ENCRYPT_MODE, AESkey(cryptKey), new IvParameterSpec(cryptKey.getBytes()));
            byte[] encrypted = c.doFinal(pan.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encode(encrypted));
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    private static Key AESkey(String key) {
        String aesKey;
        aesKey = key.substring(0, 16);

        byte[] keyBytes = new byte[16];
        byte[] b = aesKey.getBytes(StandardCharsets.UTF_8);
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);

        return new SecretKeySpec(keyBytes, "AES");
    }


    public static Boolean isValidCreditCardNumber(String cardNumber) {
        int[] cardIntArray = new int[cardNumber.length()];

        for (int i = 0; i < cardNumber.length(); i++) {
            char c = cardNumber.charAt(i);
            cardIntArray[i] = Integer.parseInt("" + c);
        }

        for (int i = cardIntArray.length - 2; i >= 0; i = i - 2) {
            int num = cardIntArray[i];
            num = num * 2;  // step 1

            if (num > 9) {
                num = num % 10 + num / 10;  // step 2
            }
            cardIntArray[i] = num;
        }
        int sum = sumDigits(cardIntArray);  // step 3
        return sum % 10 == 0;
    }

    private static int sumDigits(int[] arr) {
        return Arrays.stream(arr).sum();
    }


    public static String regexUzCard() {
        return "(8600\\d{12}$)|(626\\d{13}$)|(5614\\d{12}$)|(5440\\d{12}$)|(277\\d{13}$)";
    }

    public static String regexHumo() {
        return "(9860\\d{12}$)|(5555\\d{12}$)|(4294\\d{12}$)|(4790\\d{12}$)|(4073\\d{12}$)|(4166\\d{12}$)|(4198\\d{12}$)|(4097\\d{12}$)|(4062\\d{12}$)|(4728\\d{12}$)|(4008\\d{12}$)|(4097\\d{12}$)|(4187\\d{12}$)|(4067\\d{12}$)|(4027\\d{12}$)|(4242\\d{12}$)|(4916\\d{12}$)";
    }

    public static String regexDavrUzCard() {
        return "(860051\\d{10}$)|(626273\\d{10}$)|(56146805\\d{8}$)|(56146861\\d{8}$)";
    }

    public static String regexDavrHumo() {
        return "(986025\\d{10}$)|(42943400\\d{8}$)";
    }

    public static String regexHuMoToken() {
        return "(9860\\d{2}[A-Z]{6}\\d{4})|(5555\\d{2}[A-Z]{6}\\d{4})|(4294\\d{2}[A-Z]{6}\\d{4})|(4790\\d{2}[A-Z]{6}\\d{4})|(4073\\d{2}[A-Z]{6}\\d{4})|(4166\\d{2}[A-Z]{6}\\d{4})|(4198\\d{2}[A-Z]{6}\\d{4})|(4062\\d{2}[A-Z]{6}\\d{4})|(4728\\d{2}[A-Z]{6}\\d{4})|(4008\\d{2}[A-Z]{6}\\d{4})|(4097\\d{2}[A-Z]{6}\\d{4})|(4187\\d{2}[A-Z]{6}\\d{4})|(4067\\d{2}[A-Z]{6}\\d{4})|(4097\\d{2}[A-Z]{6}\\d{4})|(4027\\d{2}[A-Z]{6}\\d{4}$)|(4916\\d{2}[A-Z]{6}\\d{4}$)|(4242\\d{2}[A-Z]{6}\\d{4}$)";
    }

    public static String regexUzCardToken() {
        return "^(?=.{32}$)[A-Z0-9]*$";
    }


    public static String regexVisaDAVR() {
        return "(4602\\d{12}$)|(4909\\d{12}$)";
    }

    public static String regexVisaDAVRToken() {
        return "(460294[A-Z]{6}\\d{4}$)|(490957[A-Z]{6}\\d{4}$)";
    }

    public static String regexDavrMasterCardToken() {
        return "^(?=.{32}$)[A-Z0-9]*$";
    }

    public static String regexMasterCardDAVR() {
        return "(^53870354\\d{8}$)|(^52737567\\d{8}$)|(^54492371\\d{8}$)|(^52737591\\d{8}$)|(^52737515\\d{8}$)";
    }

    public static boolean isSumPC(Processing processing) {
        return processing.equals(Processing.HUMO)
                || processing.equals(Processing.UZCARD)
                || processing.equals(Processing.DAVR_MASTER_CARD)
                || processing.equals(Processing.SVGATE_AUTOPAY);
    }

    public static boolean isCurrencyPC(Processing processing) {
        return processing.equals(Processing.DAVR_VISA);
    }
}
