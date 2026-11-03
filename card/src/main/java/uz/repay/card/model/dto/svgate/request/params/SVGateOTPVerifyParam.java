package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.uzcard.request.VerifyRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SVGateOTPVerifyParam {
    @JsonProperty("otp")
    private SVGateOTPParam otp;

    public SVGateOTPVerifyParam(VerifyRequest request) {
        this.otp = new SVGateOTPParam(request);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SVGateOTPParam {
        @JsonProperty("id")
        private String id;
        @JsonProperty("code")
        private String code;

        public SVGateOTPParam(VerifyRequest request) {
            this.id = request.id();
            this.code = request.code();
        }
    }
}
