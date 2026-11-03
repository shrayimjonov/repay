package uz.nodir.notification.model.dto.template.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.dto.core.TextProp;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateTextPropParam {
    @NotBlank
    @JsonProperty("text")
    @Schema(name = "text", example = "Оплата по кредиту # в АБС", requiredMode = Schema.RequiredMode.REQUIRED, description = "text template")
    private String text;

    @NotNull
    @NotEmpty
    @JsonProperty("values")
    @Valid
    @ArraySchema(
            schema = @Schema(
                    name = "values",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "values order and their type"),
            uniqueItems = true)
    private List<TemplateValueParam> values;

    public TemplateTextPropParam(TextProp prop) {
        this.text = prop.getText();
        this.values = prop.getValueType()
                .entrySet()
                .stream()
                .map(TemplateValueParam::new)
                .toList();
    }
}
