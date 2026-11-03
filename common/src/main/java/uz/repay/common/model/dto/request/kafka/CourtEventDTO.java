package uz.repay.common.model.dto.request.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uz.repay.common.model.dto.request.kafka.base.BaseEventDTO;
import uz.repay.common.model.enums.Language;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CourtEventDTO extends BaseEventDTO {

    @JsonProperty("message")
    private String message;

    public String toJson() {
        return """
                {
                  "loan_ext_id": "%s",
                  "phone": "%s",
                  "language": "%s",
                  "message": "%s"
                }"""
                .formatted(
                        this.getLoanExtId(),
                        this.getPhone(),
                        this.getLanguage(),
                        this.message
                );
    }

    @Override
    public String[] fieldValues() {
        return new String[]{this.message, this.getLoanExtId(), this.getPhone(), this.getLanguage().name()};
    }

    public CourtEventDTO(String loanExtId, String phone, String message) {
        super(loanExtId, phone, Language.defaultLang());
        this.message = message;
    }
}
