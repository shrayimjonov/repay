package uz.repay.card.model.dto.huMoMiddleware.response.result;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoCardsByPinflResult {

    @JsonAlias(value = {"data", "DATA"})
    private List<HuMoCardsByPinflData> data;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HuMoCardsByPinflData {
        @JsonAlias(value = {"pan", "PAN"})
        private String pan;

        @JsonAlias(value = {"expiry", "EXPIRY"})
        private String expiry;

        @JsonAlias(value = {"availableAmount", "AVAILABLEAMOUNT"})
        public Long availableAmount;

        @JsonAlias(value = {"availableAmount", "AVAILABLEAMOUNT"})
        private HumoCardInfoResponse.Statuses status;

        @JsonAlias(value = {"mb", "MB"})
        private HumoCardInfoResponse.Mb mb;
    }


}
