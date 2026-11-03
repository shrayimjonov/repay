package uz.repay.loan.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.model.dto.core.note.request.NoteCreateRequestDTO;
import uz.repay.loan.model.dto.core.note.request.NoteUpdateRequestDTO;
import uz.repay.loan.model.dto.core.note.response.NoteBaseResponseDTO;
import uz.repay.loan.model.entity.Note;
import uz.repay.loan.service.business.NoteService;
import uz.repay.loan.service.core.LoanDAO;
import uz.repay.loan.service.core.NoteDAO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteDAO noteDAO;
    private final LoanDAO loanDAO;

    @Override
    public ResultData<CoreError> create(@NotNull NoteCreateRequestDTO requestDTO) {
        log.info("Received command to create note with request: {}", requestDTO);

        var loanResult = loanDAO.getOptionalByExtId(requestDTO.getLoanExtId());

        if (loanResult.isEmpty()) {
            var message = "Loan with extId: %s not found!".formatted(requestDTO.getLoanExtId());
            log.error(message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }


        var preparedNoteEntity = new Note(
                requestDTO.getLoanExtId(),
                requestDTO.getNote()
        );

        noteDAO.save(preparedNoteEntity);

        var response =
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                );

        log.info("Finished command to create note with response: {}", response);
        return response;
    }


    @Override
    public ResultData<List<NoteBaseResponseDTO>> findNotesByLoanExtId(@NotNull String loanExtId) {
        log.info("Received command to find notes by loanExtId: {}", loanExtId);

        var notes = noteDAO.findByLoanExtId(loanExtId);

        if (notes.isEmpty()) {
            log.info("Notes for: {} is empty!", loanExtId);
            return ResultData.ok(List.of());
        }

        var response = ResultData.ok(
                notes
                        .stream()
                        .map(NoteBaseResponseDTO::new)
                        .toList()
        );

        log.info("Finished command to find notes with response size: {}", response.getResult().size());
        return response;
    }

    @Override
    public ResultData<CoreError> updateById(@NotNull NoteUpdateRequestDTO requestDTO) {
        log.info("Received command to update note with request: {}", requestDTO);
        var noteResult = validateNoteCreatedUser(requestDTO.getId());

        if (!noteResult.getStatus())
            return new ResultData<>(noteResult.getError().getCode(), noteResult.getError().getMessage());

        var note = noteResult.getData();


        updateNote(requestDTO, note);

        var response =
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                );

        log.info("Finished command to update note with response: {}", response);
        return response;
    }

    private void updateNote(NoteUpdateRequestDTO requestDTO, Note note) {
        note.setText(requestDTO.getNote());
        noteDAO.save(note);
    }

    @Override
    public ResultData<CoreError> deleteById(@NotNull Long id) {
        log.info("Received command to delete note by id: {}", id);

        var noteResult = validateNoteCreatedUser(id);

        if (!noteResult.getStatus())
            return new ResultData<>(noteResult.getError().getCode(), noteResult.getError().getMessage());

        var note = noteResult.getData();

        noteDAO.deleteById(id);

        var response =
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                );

        log.info("Finished command to delete note with response: {}", response);
        return response;
    }

    private SimpleResponse<Note> validateNoteCreatedUser(Long id) {
        var username = SecurityUtils.getUsername();
        var userResult = SecurityUtils.checkUserToReal(username);

        if (!userResult.getStatus()) {
            var message = "User %s is not real!".formatted(userResult);
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        var noteResult = noteDAO.findById(id);

        if (noteResult.isEmpty()) {
            var message = "Note with id: %d not found!".formatted(id);
            log.error(message);
            return new SimpleResponse<>(ResultState.EntityNotFound.getCode(), message);
        }

        var note = noteResult.get();

        if (!note.getCreatedBy().equals(username)) {
            var message = "Note created by: %s! You can't edit this!".formatted(note.getCreatedBy());
            log.info(message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        return new SimpleResponse<>(noteResult.get());
    }
}
