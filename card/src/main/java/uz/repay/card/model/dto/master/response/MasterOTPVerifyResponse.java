package uz.repay.card.model.dto.master.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterOTPVerifyResponse {
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("id")
    private UUID id;
}
