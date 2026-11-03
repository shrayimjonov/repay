package uz.repay.common.model.dto.request.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ProtocolType;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogProtocolEventDTO {
    @JsonProperty("loan_ext_id")
    private String loanExtId;
    @JsonProperty("type")
    private ProtocolType type;
    @JsonProperty("request")
    private Map<Object, Object> request;
    @JsonProperty("ip_address")
    private String ipAddress;
    @JsonProperty("username")
    private String username;

    public String toJson() {
        return "{\n" +
                "    \"loan_ext_id\" : \"" + this.loanExtId + "\",\n" +
                "    \"type\" : \"" + this.type + "\",\n" +
                "    \"request\" : \"" + this.request + "\",\n" +
                "    \"ip_address\" : \"" + this.ipAddress + "\",\n" +
                "    \"username\" : \"" + this.username + "\"\n" +
                "}";
    }
}
