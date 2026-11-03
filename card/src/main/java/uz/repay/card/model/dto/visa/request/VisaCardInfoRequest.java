package uz.repay.card.model.dto.visa.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisaCardInfoRequest {
    private String card;
    private String expiry;
    private String type = "PAN";

}
