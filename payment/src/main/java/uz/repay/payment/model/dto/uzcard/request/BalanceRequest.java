package uz.repay.payment.model.dto.uzcard.request;

import java.util.ArrayList;

public record BalanceRequest(
        ArrayList<String> token
) {
}
