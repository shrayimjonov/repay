package uz.repay.payment.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCIBaseResponse {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("detail")
    private String generalId;
    @JsonProperty("params")
    private Map<String, String> params;

    public NCIBaseResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
