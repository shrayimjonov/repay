package uz.davrbank.repay.loggingrest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiLogFilter extends PageParams {
    private String service;
    private Boolean success;
    private String username;
    private String clientIp;
    private Integer sinceMinutes;
}
