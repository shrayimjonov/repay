package uz.repay.payment.model.dto.uzcard.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UzCardBaseResponse <R> {
    @JsonProperty("jsonrpc")
    public String jsonRPC;
    @JsonProperty("id")
    public String id;
    @JsonProperty("result")
    public R result;
    @JsonProperty("error")
    public CoreError error;
}
