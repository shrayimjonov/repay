package uz.repay.common.model.dto.request.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uz.repay.common.model.dto.request.kafka.base.SimpleEventDTO;
import uz.repay.common.model.enums.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CardConfirmationEventDTO extends SimpleEventDTO {

    @JsonProperty("code")
    private String code;


    public String toJson() {
        return "{\n" +
                "    \"loan_ext_id\" : \"" + this.getLoanExtId() + "\",\n" +
                "    \"code\" : \"" + this.code + "\",\n" +
                "    \"language\" : \"" + this.getLanguage() + "\",\n" +
                "    \"phone\" : \"" + this.getPhone() + "\",\n" +
                "    \"pan\" : \"" + this.getPan() + "\"\n" +
                "}";
    }

    @Override
    public String[] fieldValues() {
        return new String[]{this.getLoanExtId(), this.getPhone(), this.getPan()};
    }

    public CardConfirmationEventDTO(String loanExtId, String code, String pan, Language language, String phone) {
        super(loanExtId, phone, language, pan);
        this.code = code;
    }
}
