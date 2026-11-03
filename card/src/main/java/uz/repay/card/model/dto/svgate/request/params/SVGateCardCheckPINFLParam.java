package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.uzcard.request.CheckCard2PinflRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SVGateCardCheckPINFLParam {
    @JsonProperty("cardinfo")
    private SVGateCardCheckInfo cardInfo;

    public SVGateCardCheckPINFLParam(CheckCard2PinflRequest request) {
        this.cardInfo = new SVGateCardCheckInfo(request);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SVGateCardCheckInfo {
        @JsonProperty("cardId")
        private String token;
        @JsonProperty("pinfl")
        private String personCode;

        public SVGateCardCheckInfo(CheckCard2PinflRequest request) {
            this.token = request.getToken();
            this.personCode = request.getPersonCode();
        }
    }
}
