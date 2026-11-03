package uz.repay.card.util;

import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.common.utils.CardUtils;

import java.time.YearMonth;
import java.util.Objects;

public final class HumoBalanceEligibilityUtils {
    private HumoBalanceEligibilityUtils() {
    }

    public static boolean isEligibleForAutodebit(HumoCardInfoResponse.HumoCardResult cardResult, long minimumBalance) {
        if (cardResult == null || cardResult.card() == null || cardResult.balance() == null) {
            return false;
        }

        var card = cardResult.card();
        if (isExpired(card.expiry())) {
            return false;
        }

        if (cardResult.mb() == null
                || cardResult.mb().state() == null
                || !"on".equalsIgnoreCase(cardResult.mb().state().trim())) {
            return false;
        }

        if (card.pinTryCount() != null && "3".equals(card.pinTryCount().trim())) {
            return false;
        }

        if (!isApproved(card)) {
            return false;
        }

        var availableAmount = cardResult.balance().availableAmount();
        return availableAmount != null && availableAmount > minimumBalance;
    }

    public static Long availableAmount(HumoCardInfoResponse.HumoCardResult cardResult) {
        return cardResult != null && cardResult.balance() != null
                ? cardResult.balance().availableAmount()
                : null;
    }

    public static String maskedPan(HumoCardInfoResponse.HumoCardResult cardResult) {
        if (cardResult == null || cardResult.card() == null || cardResult.card().primaryAccountNumber() == null) {
            return null;
        }

        var pan = cardResult.card().primaryAccountNumber();
        return pan.matches("\\d{16}") ? CardUtils.maskPanDigits(pan) : pan;
    }

    static boolean isExpired(String expiry) {
        if (expiry == null || !expiry.matches("\\d{4}")) {
            return true;
        }

        var year = 2000 + Integer.parseInt(expiry.substring(0, 2));
        var month = Integer.parseInt(expiry.substring(2, 4));
        if (month < 1 || month > 12) {
            return true;
        }

        return YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    private static boolean isApproved(HumoCardInfoResponse.HumoCard card) {
        if (card.statuses() == null || card.statuses().item() == null) {
            return false;
        }

        return card.statuses().item().stream()
                .filter(Objects::nonNull)
                .filter(item -> "card".equalsIgnoreCase(item.type()))
                .map(HumoCardInfoResponse.Item::actionCode)
                .filter(Objects::nonNull)
                .anyMatch(HumoBalanceEligibilityUtils::isApprovedActionCode);
    }

    private static boolean isApprovedActionCode(String actionCode) {
        try {
            return Integer.parseInt(actionCode.trim()) == 0;
        } catch (NumberFormatException error) {
            return false;
        }
    }
}