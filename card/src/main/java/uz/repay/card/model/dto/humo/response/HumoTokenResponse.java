package uz.repay.card.model.dto.humo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uz.repay.card.model.dto.huMoMiddleware.response.result.HuMoMaskedPanResult;


@JsonIgnoreProperties(ignoreUnknown = true)
public record HumoTokenResponse(
        @JsonProperty("masked_pan")
        String maskedPan) {
    public HumoTokenResponse(HuMoCardBaseResponse<HuMoMaskedPanResult> response) {
        this(
                response.getResult().getCard()
        );
    }
}
