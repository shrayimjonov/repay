package uz.repay.payment.model.dto.uzcard.request;


public record P2PInfoRequest(String cardPan,
                             String expiry, String phone) {
}
