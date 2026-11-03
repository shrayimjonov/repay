package uz.repay.card.model.dto.elma.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.nciBilling.response.NCIClientInfoByPersonCode;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElmaVisaCardFindRequest {

    @JsonProperty("ListOfBranchCustomerPair")
    private List<ElmaVisaCardFindParam> clientIDs;

    public ElmaVisaCardFindRequest builder(List<NCIClientInfoByPersonCode> result) {
        this.clientIDs = result
                .stream()
                .map(z -> new ElmaVisaCardFindParam(
                                z.getId().substring(0, 5),
                                z.getId().substring(5)
                        )
                )
                .toList();
        return this;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ElmaVisaCardFindParam {
        @JsonProperty("Branch")
        private String branch;
        @JsonProperty("CustomerId")
        private String customerId;
    }
}



