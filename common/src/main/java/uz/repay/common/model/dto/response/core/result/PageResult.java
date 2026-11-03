package uz.repay.common.model.dto.response.core.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class PageResult {
    private int size;
    private int currentPage;
    private int numberOfElements;
    private int totalPages;
    private long totalElements;
    private boolean isLast;
    private boolean isFirst;

}
