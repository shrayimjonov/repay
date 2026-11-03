package uz.repay.card.model.dto.huMoMiddleware.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoCardV3InfoRequestParam {
    @JsonProperty("primaryAccountNumber")
    private String primaryAccountNumber;
    @JsonProperty("mb_flag")
    private int mb_flag;

    public HuMoCardV3InfoRequestParam(HumoCardInfoRequest request) {
        this.primaryAccountNumber = request.cardNumber();
        this.mb_flag = 1;
    }
}
