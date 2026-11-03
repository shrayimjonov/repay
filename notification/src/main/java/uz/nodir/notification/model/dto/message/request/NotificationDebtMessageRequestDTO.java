package uz.nodir.notification.model.dto.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDebtMessageRequestDTO {
    @JsonProperty("loan_ext_id")
    @Schema(name = "loan_ext_id", example = "0098127386", description = "loan external id in ABS")
    private String loanExtId;
    @JsonProperty("amount")
    @Schema(name = "amount", example = "31240032", description = "debit amount")
    private Long amount = 0L;
    @JsonProperty("full_name")
    @Schema(name = "full_name", example = "ESHMATOV TOSHMAT TESHABOY O'G'LI", description = "debtor full name")
    private String debtor;
    @JsonProperty("language")
    @Schema(name = "language", example = "RU", description = "message language")
    private Language language;
    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", description = "phone for sending sms")
    private String phone;

    public String toJson(){
        return "{\n" +
                "    \"phone\": \"" + this.phone + "\",\n" +
                "    \"debtor\": \"" + this.debtor + "\",\n" +
                "    \"amount\": " + this.amount + ",\n" +
                "    \"language\": \"" + this.language + "\",\n" +
                "    \"loan_ext_id\": \"" + this.loanExtId + "\"\n" +
                "}";
    }
}
