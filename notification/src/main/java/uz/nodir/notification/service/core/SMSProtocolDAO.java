package uz.nodir.notification.service.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import uz.nodir.notification.model.entity.SMSProtocol;

public interface SMSProtocolDAO {

    SMSProtocol save(SMSProtocol smsProtocol);

    Page<SMSProtocol> findAll(Specification<SMSProtocol> specification, Pageable pageable);
}
