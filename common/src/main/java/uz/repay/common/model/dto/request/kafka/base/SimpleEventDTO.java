package uz.repay.common.model.dto.request.kafka.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uz.repay.common.model.enums.Language;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public abstract class SimpleEventDTO extends BaseEventDTO {
    @JsonProperty("pan")
    private String pan;

    public SimpleEventDTO(String loanExtId, String phone, Language language, String pan) {
        super(loanExtId, phone, language);
        this.pan = pan;
    }
}
