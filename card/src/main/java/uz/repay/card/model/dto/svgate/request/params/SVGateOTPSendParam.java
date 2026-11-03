package uz.repay.card.model.dto.svgate.request.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.uzcard.request.OTPRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SVGateOTPSendParam {
    @JsonProperty("card")
    private SVGateOTPCardParam param;

    public SVGateOTPSendParam(OTPRequest request) {
        this.param = new SVGateOTPCardParam(
                request
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SVGateOTPCardParam {
        @JsonProperty("pan")
        private String pan;
        @JsonProperty("expiry")
        private String expiry;
        @JsonProperty("requestorPhone")
        private String holderPhone;
        @JsonProperty("sms")
        private SVGateOTPSMSParam smsParam;

        public SVGateOTPCardParam(OTPRequest request) {
            this.pan = request.cardPan();
            this.expiry = request.expiry();
            this.holderPhone = request.phone();
            this.smsParam = new SVGateOTPSMSParam(99);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SVGateOTPSMSParam {
        @JsonProperty("ussd")
        private String ussd;
        @JsonProperty("hash")
        private String hash;
        @JsonProperty("templateId")
        private Integer templateId;
        @JsonProperty("serviceName")
        private String serviceName;

        public SVGateOTPSMSParam(int templateId) {
            this.templateId = templateId;
            this.serviceName = "repay";
        }
    }

}
