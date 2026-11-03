package uz.nodir.file.service.business;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import uz.nodir.file.model.dto.file.request.FileCreateRequestDTO;
import uz.nodir.file.model.dto.file.response.FileBaseResponseDTO;
import uz.nodir.file.model.dto.file.response.FileDownloadResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.file.FileSaveResponseDTO;

import java.util.List;

public interface FileService {

    ResultData<FileBaseResponseDTO> save(@NotNull MultipartFile file,
                                         @NotNull FileCreateRequestDTO requestDTO);

    ResultData<List<FileBaseResponseDTO>> findFileListByLoan(@NotNull String loanExtId);

    ResultData<FileDownloadResponseDTO> getFileById(@NotNull Long id);

    ResultData<FileBaseResponseDTO> deleteFileById(@NotNull Long id);

    ResultData<List<FileSaveResponseDTO>> saveV2(@NotNull MultipartFile[] files,
                                                 @NotNull String loanExtId);
}
