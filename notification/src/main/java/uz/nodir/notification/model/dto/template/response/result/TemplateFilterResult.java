package uz.nodir.notification.model.dto.template.response.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.enums.TemplateType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateFilterResult {
    @JsonProperty("id")
    @Schema(name = "id", description = "id of template", example = "27", nullable = false, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @JsonProperty("type")
    @Schema(name = "type", description = "template type", example = "DEBT_OFF", nullable = false)
    private TemplateType type;
    @JsonProperty("created_by")
    @Schema(name = "created_by", description = "person who created template", example = "user@example.com", nullable = false)
    private String createdBy;
    @JsonProperty("created_time")
    @Schema(name = "created_time", description = "date time when template created", example = "1996-08-29 00:00:00.000", nullable = false)
    private LocalDateTime createdTime;


    public TemplateFilterResult(Template template) {
        this.id = template.getId();
        this.type = template.getSmsType();
        this.createdBy = template.getCreatedBy();
        this.createdTime = template.getInitTime();
    }
}
