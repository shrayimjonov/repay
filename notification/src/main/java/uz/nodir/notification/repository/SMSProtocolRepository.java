package uz.nodir.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.nodir.notification.model.entity.SMSProtocol;

@Repository
public interface SMSProtocolRepository extends JpaRepository<SMSProtocol, Long>, JpaSpecificationExecutor<SMSProtocol> {
}
