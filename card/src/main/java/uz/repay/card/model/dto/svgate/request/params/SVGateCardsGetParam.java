package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SVGateCardsGetParam {
    @JsonProperty("ids")
    private ArrayList<String> tokenList;

    public SVGateCardsGetParam(BalanceRequest request) {
        this.tokenList = request.token();
    }
}
