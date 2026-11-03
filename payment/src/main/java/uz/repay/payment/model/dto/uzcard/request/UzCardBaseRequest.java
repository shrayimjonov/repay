package uz.repay.payment.model.dto.uzcard.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UzCardBaseRequest<P> {
    @JsonProperty("id")
    private String id;
    @JsonProperty("jsonrpc")
    public String jsonRPC;
    @JsonProperty("method")
    public String method;
    @JsonProperty("params")
    public P params;

    public Map<Object, Object> toMap() {
        return Map.of(
                "id", this.id,
                "jsonrpc", this.jsonRPC,
                "method", this.method,
                "params", this.params
        );
    }
}
