package uz.repay.card.model.dto.elma.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElmaVisaCardFindResponse {
    @JsonProperty("res")
    private Integer res;

    @JsonProperty("err")
    private String err;

    @JsonProperty("Cards")
    private List<ElmaVisaCardResult> cards;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ElmaVisaCardResult {
        @JsonProperty("Branch")
        private String branch;
        @JsonProperty("CardNum")
        private String cardNum;
        @JsonProperty("CardAccount")
        private String cardAccount;
        @JsonProperty("CardStatus")
        private String cardStatus;
        @JsonProperty("CardStatusName")
        private String cardStatusName;
        @JsonProperty("CardExpiry")
        private String cardExpiry;
        @JsonProperty("CardTypeName")
        private String cardTypeName;
    }

}
