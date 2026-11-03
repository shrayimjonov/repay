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
public class TransPayParam {
    @JsonProperty("tran")
    private Tran tran;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tran {
        @JsonProperty("amount")
        private Long amount;
        @JsonProperty("ext")
        private String ext;
        @JsonProperty("terminalId")
        private String terminalId;
        @JsonProperty("merchantId")
        private String merchantId;
        @JsonProperty("cardId")
        private String cardId;
        @JsonProperty("stan")
        private String stan;
        @JsonProperty("port")
        private Integer port;

    }
}

