package uz.nodir.notification.model.dto.template.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.enums.ValueType;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateValueParam {

    @NotNull
    @Min(1)
    @JsonProperty("order")
    @Schema(name = "order", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, description = "order of value")
    private Integer order = 0;

    @NotNull
    @JsonProperty("value_type")
    @Schema(name = "value_type", example = "LOAN", requiredMode = Schema.RequiredMode.REQUIRED, description = "type of value")
    private ValueType valueType;

    public TemplateValueParam(Map.Entry<Integer, ValueType> p) {
        this.order = p.getKey();
        this.valueType = p.getValue();
    }
}
