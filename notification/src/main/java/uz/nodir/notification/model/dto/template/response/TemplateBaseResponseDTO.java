package uz.nodir.notification.model.dto.template.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.dto.template.request.params.TemplateParams;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.enums.TemplateType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateBaseResponseDTO {
    @JsonProperty("id")
    @Schema(name = "id", description = "template id", example = "12")
    private Long id;

    @JsonProperty("type")
    @Schema(name = "type", description = "template type", example = "MIB")
    private TemplateType type;

    @JsonProperty("params")
    @Valid
    @ArraySchema(
            schema = @Schema(
                    name = "params", requiredMode = Schema.RequiredMode.REQUIRED, description = "params of template text"
            ),
            uniqueItems = true
    )
    private List<TemplateParams> params;

    public TemplateBaseResponseDTO(Template template) {
        this.id = template.getId();
        this.type = template.getSmsType();
        this.params = template.getTextValues()
                .entrySet()
                .stream()
                .map(TemplateParams::new)
                .toList();
    }
}
