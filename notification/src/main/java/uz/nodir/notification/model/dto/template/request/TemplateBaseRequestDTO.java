package uz.nodir.notification.model.dto.template.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.dto.template.request.params.TemplateParams;
import uz.nodir.notification.model.enums.TemplateType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateBaseRequestDTO {
    @NotNull
    @JsonProperty("type")
    @Schema(name = "type", example = "PUSH", requiredMode = Schema.RequiredMode.REQUIRED, description = "type of template")
    private TemplateType type;
    @NotEmpty
    @NotNull
    @JsonProperty("params")
    @Valid
    @ArraySchema(
            schema = @Schema(
                    name = "params", requiredMode = Schema.RequiredMode.REQUIRED, description = "params of template text"
            ),
            uniqueItems = true
    )
    private List<TemplateParams> params;
}
