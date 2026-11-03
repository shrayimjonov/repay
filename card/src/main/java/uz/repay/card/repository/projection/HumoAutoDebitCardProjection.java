package uz.repay.card.repository.projection;

public interface HumoAutoDebitCardProjection {
    Long getCardId();

    String getPan();

    String getMaskedPan();
}