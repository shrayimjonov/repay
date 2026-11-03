package uz.repay.loan.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.repay.loan.model.entity.Note;
import uz.repay.loan.repository.NoteRepository;
import uz.repay.loan.service.core.NoteDAO;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteDAOImpl implements NoteDAO {
    private final NoteRepository repository;

    @Override
    @Transactional
    public Note save(Note note) {
        log.info("Save/Update note entity: {}", note);
        return repository.save(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByLoanExtId(String loanExtId) {
        log.info("Find notes by loanExtId: {}", loanExtId);
        return repository.findAllByLoanExtId(loanExtId);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findById(Long id) {
        log.info("Find note by id: {}", id);
        return repository.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Delete note by id: {}", id);
        repository.deleteById(id);
    }

}
