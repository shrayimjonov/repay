package uz.repay.card.model.dto.uzcard.request;

import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;


public record OTPRequest(
        String cardPan,
        String expiry,
        String phone) {

    public OTPRequest(OTPRequestDTO requestDTO) {
        this(
                requestDTO.getCard(),
                requestDTO.getExpiry(),
                requestDTO.getPhone()
        );
    }
}
