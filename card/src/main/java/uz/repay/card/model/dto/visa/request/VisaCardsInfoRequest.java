package uz.repay.card.model.dto.visa.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisaCardsInfoRequest {
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("card_ids")
    private ArrayList<String> cardIds;
}
