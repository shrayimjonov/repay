package uz.repay.payment.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.exception.CardException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;

import uz.repay.payment.model.dto.nciBilling.response.NCIDayResponse;
import uz.repay.payment.model.entity.Transaction;

import java.lang.reflect.Field;
import java.time.LocalDate;

@Slf4j
public class TranUtils {
    public static void checkTranDate(Transaction transaction) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            if (SecurityUtils.extractRoles().stream().noneMatch(z -> z.equals("ROLE_ADMIN"))) {
                if (transaction.getCreatedDate().plusDays(30).isBefore(LocalDate.now())) {
                    throw new InvalidDataException("You can't do operation with transaction before this month! Transaction date: %s".formatted(transaction.getCreatedDate()));
                }
            } else
                log.info("User: {} with ADMIN_ROLE wants to do operation with transaction. Permission accessed", SecurityUtils.getUsername());

        } else
            log.info("Check tran date accessed. Cause requester is system");
    }


    public static void validateStatus(ResponseStatus status) {

        if (status.equals(ResponseStatus.SENDER_SUCCESS) || status.equals(ResponseStatus.RECIPIENT_SUCCESS))
            throw new InvalidDataException("Please wait finishing transaction!");
    }

    public static boolean dayResult(SimpleResponse<NCIDayResponse> response, String branch) {
        if (!response.getStatus()) {
            log.error("Bank day fetching error");
            return false;
        }

        var originalResponse = response.getData();

        try {
            Field myField = originalResponse.getClass().getDeclaredField("_" + branch);
            var result = (Boolean) myField.get(originalResponse);
            log.info("{} day status is {}", branch, result);
            return result;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.info("Day parse error: {}", e.getMessage());
            return false;
        }
    }


    public static Currency takeCurrencyByPC(Processing processing) {
        switch (processing) {
            case UZCARD, HUMO, SVGATE_AUTOPAY, DAVR_MASTER_CARD -> {
                return Currency.UZS;
            }
            case DAVR_VISA -> {
                return Currency.USD;
            }
            default -> throw new CardException("Not detected currency for pc: %s".formatted(processing));

        }
    }
}
