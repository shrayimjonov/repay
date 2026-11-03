package uz.nodir.file.service.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.nodir.file.model.entity.Location;

import java.util.Optional;

public interface LocationDAO {

    Location save(Location location);

    Page<Location> findAllByLoanExtId(String loanExtId, Pageable pageable);

    Optional<Location> findById(Long id);

    void deleteById(Long id);
}
