package nodir.paymo_add.repository;

import nodir.paymo_add.entity.Sms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmsRepository extends JpaRepository<Sms,Long> {
    @Query(value = "select * FROM sms.message m WHERE m.recipient=:phone order BY m.instime desc", nativeQuery = true)
    List<Sms> findAllSmsByPhone(String phone);
}
