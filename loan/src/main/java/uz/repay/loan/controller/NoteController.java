package uz.repay.loan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.note.request.NoteCreateRequestDTO;
import uz.repay.loan.model.dto.core.note.request.NoteUpdateRequestDTO;
import uz.repay.loan.model.dto.core.note.response.NoteBaseResponseDTO;
import uz.repay.loan.service.business.NoteService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/note", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "note-methods", description = "Loan note endpoints")
public class NoteController {
    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "Create note", description = "Create notes for loan", operationId = "note-loan-create")
    public ResponseEntity<ResultData<CoreError>> create(@RequestBody @Valid NoteCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(noteService.create(requestDTO));
    }

    @GetMapping
    @Operation(summary = "Find notes by LoanExtId", description = "Find notes list by loanExtId", operationId = "note-searching-byLoan")
    public ResponseEntity<ResultData<List<NoteBaseResponseDTO>>> findNotesByLoanExtId(@RequestParam("loanExtId") String loanExtId) {
        return ResponseEntity.ok(noteService.findNotesByLoanExtId(loanExtId));
    }

    @PutMapping
    @Operation(summary = "Note update", description = "Update note by noteId", operationId = "note-update-byId")
    public ResponseEntity<ResultData<CoreError>> updateById(@RequestBody @Valid NoteUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(noteService.updateById(requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Note delete", description = "Delete note by noteId", operationId = "note-delete-byId")
    public ResponseEntity<ResultData<CoreError>> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(noteService.deleteById(id));
    }


}
