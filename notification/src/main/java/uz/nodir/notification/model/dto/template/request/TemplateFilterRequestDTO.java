package uz.nodir.notification.model.dto.template.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.dto.template.request.params.TemplateFilterParams;
import uz.repay.common.model.dto.request.core.params.PageParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TemplateFilterRequestDTO extends PageParams {
    @JsonProperty("params")
    @Schema(name = "params", description = "template filter params", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TemplateFilterParams params;
}
