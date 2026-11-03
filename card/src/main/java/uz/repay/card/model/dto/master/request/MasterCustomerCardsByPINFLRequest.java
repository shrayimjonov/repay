package uz.repay.card.model.dto.master.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterCustomerCardsByPINFLRequest {
    @JsonProperty("person_code")
    private String person_code;
}
