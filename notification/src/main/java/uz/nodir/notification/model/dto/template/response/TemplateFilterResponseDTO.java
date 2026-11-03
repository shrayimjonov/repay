package uz.nodir.notification.model.dto.template.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.nodir.notification.model.dto.template.response.result.TemplateFilterResult;
import uz.nodir.notification.model.entity.Template;
import uz.repay.common.model.dto.response.core.result.PageResult;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TemplateFilterResponseDTO extends PageResult {
    @ArraySchema(
            schema = @Schema(
                    name = "list", description = "template types list"
            ),
            uniqueItems = true
    )
    private List<TemplateFilterResult> list;

    public TemplateFilterResponseDTO(Page<Template> pageResult) {
        super(pageResult.getSize(),
                pageResult.getNumber() + 1,
                pageResult.getNumberOfElements(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.isLast(),
                pageResult.isFirst()
        );
        this.list = pageResult
                .stream()
                .map(TemplateFilterResult::new)
                .toList();
    }
}
