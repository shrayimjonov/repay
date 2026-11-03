package uz.repay.card.model.entity;

import uz.repay.card.model.data.PanFake;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;
import uz.repay.common.utils.CardUtils;
import uz.repay.common.utils.DateUtils;

public class CardEntity {

    public static Card fakeUzCard() {
        Card card = new Card();
        card.setId(22L);
        card.setPan(CardUtils.encryptPan(PanFake.UZCARD_FAKE_PAN));
        card.setExpiry("2904");
        card.setHolder("KIMDIR KIMDIROVICH");
        card.setIsActive(true);
        card.setMaskedPan(CardUtils.maskPanDigits(PanFake.UZCARD_FAKE_PAN));
        card.setProcessing(Processing.UZCARD);
        card.setIsConfirmed(true);
        card.setPhone("998901234567");
        card.setService(ServiceType.REPAY);
        card.setCreatedBy("user@example.com");
        card.setCreatedDate(DateUtils.getDateNow());
        card.setModifiedBy("user@example.com");
        card.setInitTime(DateUtils.getDateTimeNow());
        card.setUpdateTime(DateUtils.getDateTimeNow());
        card.setToken(PanFake.UZCARD_TOKEN_1);
        return card;
    }

    public static Card fakeHuMo() {
        Card card = new Card();
        card.setId(23L);
        card.setPan(CardUtils.encryptPan(PanFake.HUMO_FAKE_PAN));
        card.setExpiry("2810");
        card.setHolder("HUMO HUMOYEVICH");
        card.setIsActive(true);
        card.setMaskedPan(CardUtils.maskPanDigits(PanFake.HUMO_FAKE_PAN));
        card.setProcessing(Processing.HUMO);
        card.setIsConfirmed(true);
        card.setPhone("998901234567");
        card.setService(ServiceType.REPAY);
        card.setCreatedBy("user@example.com");
        card.setCreatedDate(DateUtils.getDateNow());
        card.setModifiedBy("user@example.com");
        card.setInitTime(DateUtils.getDateTimeNow());
        card.setUpdateTime(DateUtils.getDateTimeNow());
        card.setToken(PanFake.HUMO_FAKE_TOKEN_1);
        return card;
    }


}
