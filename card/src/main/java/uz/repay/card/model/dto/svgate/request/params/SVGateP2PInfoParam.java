package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SVGateP2PInfoParam {
    @JsonProperty("hpan")
    private String pan;

    public SVGateP2PInfoParam(P2PInfoRequest request) {
        this.pan = request.cardPan();
    }
}
