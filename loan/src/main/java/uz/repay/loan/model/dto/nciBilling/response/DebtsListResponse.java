package uz.repay.loan.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebtsListResponse {
    @JsonProperty("inf")
    private ArrayList<Info> info;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        @JsonProperty("recommended_amount")
        private Long recommendedAmount;
        @JsonProperty("max_amount")
        private Long maxAmount;
        @JsonProperty("branch")
        private String branch;
        @JsonProperty("id")
        private String loanId;
    }

}

