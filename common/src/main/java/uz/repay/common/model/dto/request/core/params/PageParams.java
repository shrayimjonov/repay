package uz.repay.common.model.dto.request.core.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class PageParams {
    @Schema(name = "pageNumber", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "It means which page number selected")
    private int pageNumber = 0;
    @Schema(name = "pageSize", example = "20", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "It means how many data selected")
    private int pageSize = 3;
    @Schema(name = "sortBy", example = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "sort by field name")
    private String sortBy = "id";
    @Schema(name = "descending", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "desc or asc sorting")
    private boolean descending = true;

}
