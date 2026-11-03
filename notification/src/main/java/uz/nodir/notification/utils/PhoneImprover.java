package uz.nodir.notification.utils;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PhoneImprover {
    public static Boolean checkPhoneNumber(String phoneNumber) {
        log.info("Checking phone: {}", phoneNumber);
        if (!phoneNumber.matches("\\d{12}"))
            return false;


        return !isTheSameDigits(phoneNumber.substring(5));
    }

    public static List<String> getOnlyValidNumbers(List<String> phoneList) {
        List<String> newList = new ArrayList<>();
        for (var phone : phoneList) {
            if (phone == null || phone.isEmpty()) {
                log.warn("Phone can't be null or empty");
                continue;
            }
            phone = CommonUtils.phoneImprover(phone);
            if (checkPhoneNumber(phone))
                newList.add(phone);
            else {
                log.error("Phone number : {} is not valid!", phone);
            }
        }
        return newList;
    }

    public static Boolean isTheSameDigits(String phone){
        return phone.matches("\\b(\\d)\\1+\\b");
    }

}
