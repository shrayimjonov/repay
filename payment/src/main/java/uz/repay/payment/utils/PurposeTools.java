package uz.repay.payment.utils;

import lombok.extern.slf4j.Slf4j;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.PurposeValues;

@Slf4j
public class PurposeTools {

    public static String purposeBuilder(Transaction t, String purpose) {
        log.info("Change purpose: {}", purpose);


        var rrn = t.getRefNum() != null ? t.getRefNum() : "";
        var pan = t.getCardPan() != null ? t.getCardPan() : "";
        var amount = t.getAmount() != null ? t.getAmount().toString() : "";
        var terminal = t.getTerminalId() != null ? t.getTerminalId() : "";
        var cardHolder = t.getCardOwner() != null ? t.getCardOwner() : "";
        var loanExtId = t.getLoanExtId() != null ? t.getLoanExtId() : "";
        var rate = t.getRate() !=null ? t.getRate().toString() : "";

        purpose = purpose.replace(PurposeValues.RRN.getCode(), rrn);
        purpose = purpose.replace(PurposeValues.CARD.getCode(), pan);
        purpose = purpose.replace(PurposeValues.AMOUNT.getCode(), amount);
        purpose = purpose.replace(PurposeValues.TERMINAL.getCode(), terminal);
        purpose = purpose.replace(PurposeValues.CARD_HOLDER.getCode(), cardHolder);
        purpose = purpose.replace(PurposeValues.LOAN_ID.getCode(), loanExtId);
        purpose = purpose.replace(PurposeValues.RATE.getCode(), rate);

        return purpose;
    }
}
