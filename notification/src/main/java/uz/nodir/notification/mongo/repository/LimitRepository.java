package uz.nodir.notification.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.mongo.entity.Limit;

import java.util.List;
import java.util.UUID;


@Repository
public interface LimitRepository extends CrudRepository<Limit, UUID> {

    List<Limit> findByLoanExtIdAndPhoneAndTemplateType(String loanExtId, String phone, TemplateType templateType);
}
