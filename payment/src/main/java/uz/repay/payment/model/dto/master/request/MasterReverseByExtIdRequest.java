package uz.repay.payment.model.dto.master.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterReverseByExtIdRequest {
    @JsonProperty("extId")
    private String extId;

    public Map<Object, Object> toMap() {
        return Map.of(
                "extId", this.extId
        );
    }
}
