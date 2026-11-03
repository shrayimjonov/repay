package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SVGateAutoPaySubscribeParam {
    @JsonProperty("allinfo")
    private SVGateAutoPaySubscribeCard clientInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SVGateAutoPaySubscribeCard {
        @JsonProperty("cardId")
        private String token;
        @JsonProperty("subscribe")
        private Integer subscribe;

    }
}
