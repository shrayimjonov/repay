package uz.nodir.notification.model.dto.template.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.enums.TemplateType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateFilterParams {
    @JsonProperty("types")
    @ArraySchema(
            schema =
            @Schema(
                    name = "types", requiredMode = Schema.RequiredMode.REQUIRED, description = "template type", example = "DEBT_OFF"
            )
    )
    private List<TemplateType> types;
}
