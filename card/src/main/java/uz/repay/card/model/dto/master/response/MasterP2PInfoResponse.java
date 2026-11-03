package uz.repay.card.model.dto.master.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterP2PInfoResponse {
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("token")
    private String token;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("username")
    private String username;
    @JsonProperty("holder")
    private String holder;
    @JsonProperty("pinCount")
    private Integer pinCount;
    @JsonProperty("type")
    private String type;
}
