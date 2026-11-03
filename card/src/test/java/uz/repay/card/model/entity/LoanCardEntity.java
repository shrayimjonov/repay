package uz.repay.card.model.entity;

import uz.repay.card.model.entity.pk.LoanCardKey;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class LoanCardEntity {

    public static List<LoanCard> fakeUzCardLoanCards() {
        List<LoanCard> loanCards = new ArrayList<>();

        var loanCard = fakeUzCardLoanCard();

        loanCards.add(loanCard);
        return loanCards;
    }

    public static LoanCard fakeUzCardLoanCard() {
        var loan = LoanEntity.loan;
        var card = CardEntity.fakeUzCard();

        var loanCard = new LoanCard();
        loanCard.setId(new LoanCardKey(loan.getId(), card.getId()));

        loanCard.setLoan(loan);
        loanCard.setCard(card);
        loanCard.setIsDeleted(false);
        loanCard.setIsConfirmed(true);
        loanCard.setPayer(Payer.BORROWER);
        loanCard.setCreatedBy("user123");
        loanCard.setCreatedDate(DateUtils.getDateNow().minusDays(1));
        loanCard.setModifiedBy("user123");
        loanCard.setInitTime(DateUtils.getDateTimeNow().minusDays(2).plusHours(5));
        loanCard.setUpdateTime(DateUtils.getDateTimeNow().minusDays(2).plusHours(5));
        loanCard.setAddType(AddType.AUTO);
        loanCard.setIpAddress("127.0.0.1");
        return loanCard;
    }


    public static LoanCard fakeHuMoLoanCard() {
        var loan = LoanEntity.loan;
        var card = CardEntity.fakeHuMo();

        var loanCard = new LoanCard();
        loanCard.setId(new LoanCardKey(loan.getId(), card.getId()));

        loanCard.setLoan(loan);
        loanCard.setCard(card);
        loanCard.setIsDeleted(false);
        loanCard.setIsConfirmed(true);
        loanCard.setPayer(Payer.BORROWER);
        loanCard.setCreatedBy("user123");
        loanCard.setCreatedDate(DateUtils.getDateNow().minusDays(1));
        loanCard.setModifiedBy("user123");
        loanCard.setInitTime(DateUtils.getDateTimeNow().minusDays(2).plusHours(5));
        loanCard.setUpdateTime(DateUtils.getDateTimeNow().minusDays(2).plusHours(5));
        loanCard.setAddType(AddType.AUTO);
        loanCard.setIpAddress("127.0.0.1");
        return loanCard;
    }

    public static List<LoanCard> fakeHuMoLoanCards() {
        List<LoanCard> loanCards = new ArrayList<>();

        var loanCard = fakeHuMoLoanCard();

        loanCards.add(loanCard);
        return loanCards;
    }
}
