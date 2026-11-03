package uz.repay.loan.service.core;

import uz.repay.loan.model.entity.Note;

import java.util.List;
import java.util.Optional;

public interface NoteDAO {

    Note save(Note note);

    List<Note> findByLoanExtId(String loanExtId);

    Optional<Note> findById(Long id);

    void deleteById(Long id);
}
