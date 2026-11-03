package uz.nodir.notification.service.core;

import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.mongo.entity.Limit;

import java.util.List;

public interface LimitDAO {

    Limit save(Limit limit);

    List<Limit> findByLoanAndPhoneAndType(String loanExtId, String phone, TemplateType type);

}
