package uz.nodir.notification.model.dto.template.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.dto.core.TextProp;
import uz.repay.common.model.enums.Language;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateParams {
    @NotNull
    @JsonProperty("language")
    @Schema(name = "language", example = "RU", requiredMode = Schema.RequiredMode.REQUIRED, description = "text language")
    private Language language;
    @NotNull
    @JsonProperty("text_prop")
    @Valid
    @Schema(name = "text_prop", requiredMode = Schema.RequiredMode.REQUIRED, description = "text properties")
    private TemplateTextPropParam textProp;

    public TemplateParams(Map.Entry<Language, TextProp> m) {
        this.language = m.getKey();
        this.textProp = new TemplateTextPropParam(m.getValue());
    }
}
