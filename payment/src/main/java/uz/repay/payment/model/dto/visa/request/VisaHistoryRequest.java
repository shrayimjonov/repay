package uz.repay.payment.model.dto.visa.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.utils.CardUtils;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisaHistoryRequest {
    @JsonProperty("card")
    private String card;
    @JsonProperty("beginDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String beginDate;
    @JsonProperty("finishDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String finishDate;
    @JsonProperty("subSystem")
    private String subSystem; //REPAY

    public Map<Object, Object> toMap() {
        return Map.of(
                "card", CardUtils.maskPanDigits(this.card),
                "beginDate", this.beginDate,
                "finishDate", this.finishDate,
                "subSystem", this.subSystem
        );
    }
}
