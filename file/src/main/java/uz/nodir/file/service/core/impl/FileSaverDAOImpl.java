package uz.nodir.file.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.nodir.file.model.entity.FileSaver;
import uz.nodir.file.repository.FileSaverRepository;
import uz.nodir.file.service.core.FileSaverDAO;
import uz.repay.common.exception.BadRequestException;
import uz.repay.common.exception.EntityNotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileSaverDAOImpl implements FileSaverDAO {
    private final FileSaverRepository repository;

    @Override
    public FileSaver save(FileSaver fileSaver) {
        log.info("Save file: {}", fileSaver);
        return repository.save(fileSaver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileSaver> findFilesByLoanExtId(String loanExtId) {
        log.info("Find files by loanExtId: {}", loanExtId);
        return repository.findByLoanExtIdAndIsInternal(loanExtId, false);
    }

    @Override
    @Transactional(readOnly = true)
    public FileSaver findById(Long id) {
        log.info("Find file by id: {}", id);
        return repository.findById(id)
                .orElseThrow(()-> new BadRequestException("File with id: %d not found in database".formatted(id)));
    }

}
