package uz.davrbank.repay.loggingrest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActiveSessionsRequest extends PageParams {
    private String username;
}
