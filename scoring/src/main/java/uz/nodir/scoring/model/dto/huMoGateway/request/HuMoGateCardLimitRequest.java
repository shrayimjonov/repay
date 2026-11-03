package uz.nodir.scoring.model.dto.huMoGateway.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.scoring.util.CommonUtil;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoGateCardLimitRequest {

    @JsonProperty("card")
    private String card;

    @JsonProperty("subSystem")
    private String subSystem = CommonUtil.HUMO_GATE_SUB_SYSTEM;

    public HuMoGateCardLimitRequest(String pan) {
        this.card = pan;
    }
}
