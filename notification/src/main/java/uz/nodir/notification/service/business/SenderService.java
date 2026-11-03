package uz.nodir.notification.service.business;

import uz.nodir.notification.model.enums.TemplateType;
import uz.repay.common.model.dto.response.core.SimpleResponse;

public interface SenderService {

    SimpleResponse<String> send(String text, String phone, TemplateType type, String loanExtId);
}
