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
public class CardAttachedEventDTO extends SimpleEventDTO {

    @JsonProperty("holder")
    private String holder;


    public String toJson() {
        return """
                {
                  "pan": "%s",
                  "loan_ext_id": "%s",
                  "holder": "%s",
                  "language": "%s",
                  "phone": "%s"
                }"""
                .formatted(this.getPan(), this.getLoanExtId(), holder, this.getLanguage(), this.getPhone());
    }

    @Override
    public String[] fieldValues() {
        return new String[]{this.getPan(), this.getLoanExtId(), this.holder, this.getPhone()};
    }

    public CardAttachedEventDTO(String loanExtId, String phone, Language language, String pan, String holder) {
        super(loanExtId, phone, language, pan);
        this.holder = holder;
    }
}
