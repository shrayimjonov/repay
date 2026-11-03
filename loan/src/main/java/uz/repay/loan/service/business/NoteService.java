package uz.repay.loan.service.business;

import jakarta.validation.constraints.NotNull;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.note.request.NoteCreateRequestDTO;
import uz.repay.loan.model.dto.core.note.request.NoteUpdateRequestDTO;
import uz.repay.loan.model.dto.core.note.response.NoteBaseResponseDTO;

import java.util.List;

public interface NoteService {
    ResultData<CoreError> create(@NotNull NoteCreateRequestDTO requestDTO);

    ResultData<List<NoteBaseResponseDTO>> findNotesByLoanExtId(@NotNull String loanExtId);

    ResultData<CoreError> updateById(@NotNull NoteUpdateRequestDTO requestDTO);

    ResultData<CoreError> deleteById(@NotNull Long id);
}
