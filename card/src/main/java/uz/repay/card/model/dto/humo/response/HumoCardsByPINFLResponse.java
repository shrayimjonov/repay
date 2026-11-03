package uz.repay.card.model.dto.humo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HumoCardsByPINFLResponse {
    private String id;
    private PINFLResult result;
    private CoreError error;

    public HumoCardsByPINFLResponse(HuMoCardBaseResponse<PINFLResult> result) {
        this.id = result.getId();
        this.result = result.getResult();
        this.error = result.getError();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PINFLResult {
        private ArrayList<PINFLData> data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PINFLData {
        private String name;
        private String card;
        private String expiry;
    }

}
