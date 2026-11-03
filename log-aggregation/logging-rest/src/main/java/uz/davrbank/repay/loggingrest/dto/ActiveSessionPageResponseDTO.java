package uz.davrbank.repay.loggingrest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.result.PageResult;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActiveSessionPageResponseDTO extends PageResult {
    private List<ActiveSession> list;

    public ActiveSessionPageResponseDTO(List<ActiveSession> content, int pageNumber, int pageSize, long totalElements) {
        super(
                pageSize,
                pageNumber + 1,
                content.size(),
                pageSize <= 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize),
                totalElements,
                ((long) pageNumber * pageSize + content.size()) >= totalElements,
                pageNumber == 0
        );
        this.list = content;
    }
}
