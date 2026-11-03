package uz.repay.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.repay.card.model.entity.EPos;

import java.util.List;

@Repository
public interface EPosRepository extends JpaRepository<EPos, Long> {

    @Modifying
    @Query("update EPos e set e.exceptional = true where e.terminalId in :terminalIds")
    void makeExceptional(@Param("terminalIds") List<String> terminalIds);
}
