package uz.repay.common.model.dto.request.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uz.repay.common.model.dto.request.kafka.base.SimpleEventDTO;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.Payer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class NotificationDebitPayEventDTO extends SimpleEventDTO {
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("payer")
    private Payer payer;

    public String toJson() {
        return "{\n" +
                "    \"phone\": \"" + this.getPhone() + "\",\n" +
                "    \"full_name\": \"" + this.fullName + "\",\n" +
                "    \"amount\": " + this.amount + ",\n" +
                "    \"language\": \"" + this.getLanguage() + "\",\n" +
                "    \"loan_ext_id\": \"" + this.getLoanExtId() + "\",\n" +
                "    \"payer\": \"" + this.payer + "\",\n" +
                "    \"pan\": \"" + this.getPan() + "\"\n" +
                "}";
    }

    @Override
    public String[] fieldValues() {
        return new String[]{this.getLoanExtId(), this.getAmount().toString(), this.getPan(), this.getPhone()};
    }

    public NotificationDebitPayEventDTO(
            String loanExtId, Language language,
            String phone,
            String pan, String fullName,
            Long amount, Payer payer
    ) {
        super(loanExtId, phone, language, pan);
        this.fullName = fullName;
        this.amount = amount;
        this.payer = payer;
    }
}
