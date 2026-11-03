package uz.nodir.file.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.file.model.entity.Location;
import uz.nodir.file.repository.LocationRepository;
import uz.nodir.file.service.core.LocationDAO;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationDAOImpl implements LocationDAO {
    private final LocationRepository repository;

    @Override
    @Transactional
    public Location save(Location location) {
        log.info("Save/Update location entity: {}", location);
        return repository.save(location);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Location> findAllByLoanExtId(String loanExtId, Pageable pageable) {
        log.info("Find locations by loanExtId: {}", loanExtId);
        return repository.findAllByLoanExtId(loanExtId, pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Location> findById(Long id) {
        log.info("Find location by id: {}", id);
        return repository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Delete location by id: {}", id);
        repository.deleteById(id);
    }


}
