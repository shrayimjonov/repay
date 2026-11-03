package uz.nodir.notification.model.dto.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.enums.TemplateType;
import uz.repay.common.model.enums.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationNoticeRequestDTO {
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("language")
    private Language language;
    @JsonProperty("type")
    private TemplateType type;
}
