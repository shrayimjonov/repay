package uz.repay.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.repay.common.model.enums.StatusType;
import uz.repay.payment.model.entity.Backlash;

import java.util.Optional;

@Repository
public interface BacklashRepository extends JpaRepository<Backlash, Long> {

    @Query(value = "select * from backlash\n" +
            "where is_used = false and type=:type \n" +
            "order by created_time desc\n" +
            "limit 1", nativeQuery = true)
    Optional<Backlash> findLastNotUsedBacklash(String type);
}
