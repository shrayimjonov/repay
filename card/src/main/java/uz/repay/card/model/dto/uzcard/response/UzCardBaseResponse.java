package uz.repay.card.model.dto.uzcard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UzCardBaseResponse<R> {
    @JsonProperty("jsonrpc")
    private String jsonRPC;
    @JsonProperty("id")
    private String id;
    @JsonProperty("result")
    private R result;
    @JsonProperty("error")
    private CoreError error;

    public UzCardBaseResponse(String s, long l, R result) {
        this.jsonRPC = s;
        this.id = String.valueOf(l);
        this.result = result;
    }
}
