package uz.repay.card.model.dto.humo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoCardVerifyRequest {
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("phone")
    private String phone;

}
