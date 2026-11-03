package uz.repay.card.model.dto.humo.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoP2PInfoResult {
    @JsonProperty("listSize")
    private Integer listSize;
    @JsonProperty("card")
    private CardInfo card;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardInfo {
        @JsonProperty("institutionId")
        private String institutionId;
        @JsonProperty("primaryAccountNumber")
        private String primaryAccountNumber;
        @JsonProperty("effectiveDate")
        private Date effectiveDate;
        @JsonProperty("updateDate")
        private Date updateDate;
        @JsonProperty("expiry")
        private String expiry;
        @JsonProperty("cardholderId")
        private String cardholderId;
        @JsonProperty("nameOnCard")
        private String nameOnCard;
        @JsonProperty("cardUserId")
        private String cardUserId;
        @JsonProperty("bankC")
        private String bankC;
        @JsonProperty("pinTryCount")
        private Integer pinTryCount;
        @JsonProperty("statuses")
        private Statuses statuses;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statuses {
        @JsonProperty("item")
        private ArrayList<Item> item;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("type")
        private String type;
        @JsonProperty("actionCode")
        private String actionCode;
        @JsonProperty("actionDescription")
        private String actionDescription;
        @JsonProperty("effectiveDate")
        private Date effectiveDate;
    }
}
