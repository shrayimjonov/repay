package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.uzcard.request.CheckCardsByPhoneRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SVGateCardsByPhoneParam {
    @JsonProperty("phone")
    private String phone;

    public SVGateCardsByPhoneParam(CheckCardsByPhoneRequest request) {
        this.phone = request.getPhone();
    }
}
