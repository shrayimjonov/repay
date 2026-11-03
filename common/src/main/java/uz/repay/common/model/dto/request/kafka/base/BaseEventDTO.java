package uz.repay.common.model.dto.request.kafka.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Language;


@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEventDTO {

    @JsonProperty("loan_ext_id")
    private String loanExtId;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("language")
    private Language language;

    public abstract String[] fieldValues();
}
