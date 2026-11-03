package uz.repay.card.model.dto.huMoMiddleware.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.humo.request.HuMoCardVerifyRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoOTPSendParam {
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("msisdn")
    private String msisdn;

    public HuMoOTPSendParam(HuMoCardVerifyRequest request) {
        this.pan = request.getPan();
        this.msisdn = request.getPhone();
    }
}
