package uz.repay.common.model.dto.request.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanPaidEventDTO {
    @JsonProperty("loanExtId")
    private String loanExtId;
    @JsonProperty("pan")
    private String pan;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("username")
    private String username;


    public String toJson() {
        return "{\n" +
                "    \"loanExtId\": \"" + this.loanExtId + "\",\n" +
                "    \"pan\": \"" + this.pan + "\",\n" +
                "    \"amount\": " + this.amount + ",\n" +
                "    \"username\": \"" + this.username + "\"\n" +
                "}";
    }
}
