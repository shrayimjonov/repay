package uz.repay.payment.model.dto.uzcard.request.params;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransPayPurposeParam {
    @JsonProperty("tran")
    private Tran tran;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tran {
        @JsonProperty("purpose")
        private String purpose;
        @JsonProperty("receiverId")
        private String receiverId;
        @JsonProperty("cardId")
        private String cardId;
        @JsonProperty("amount")
        private Long amount;
        @JsonProperty("commission")
        private Integer commission;
        @JsonProperty("currency")
        private String currency;
        @JsonProperty("ext")
        private String ext;
        @JsonProperty("merchantId")
        private String merchantId;
        @JsonProperty("terminalId")
        private String terminalId;
    }
}
