package uz.repay.card.model.dto.uzcard.request;

import java.util.ArrayList;

public record BalanceRequest(
        ArrayList<String> token
) {
}
