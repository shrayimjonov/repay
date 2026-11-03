package uz.nodir.scoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.nodir.scoring.model.entity.Limit;

import java.util.Optional;


@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    @Query(value = "select l from Limit l where l.type=:type")
    Optional<Limit> findByType(@Param("type") String type);
}
