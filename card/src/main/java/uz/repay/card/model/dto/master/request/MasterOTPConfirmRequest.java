package uz.repay.card.model.dto.master.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterOTPConfirmRequest {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("code")
    private String code;
}
