package uz.davrbank.repay.loggingrest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.PageParams;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActiveUsersIpRequest extends PageParams {
    private LocalDateTime from;
    private String username;
}
