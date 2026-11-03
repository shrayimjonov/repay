package uz.nodir.file.endpoint.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.nodir.file.model.dto.file.request.FileCreateRequestDTO;
import uz.nodir.file.model.dto.file.response.FileBaseResponseDTO;
import uz.nodir.file.service.business.FileService;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.file.FileSaveResponseDTO;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/result", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "file-methods")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "File Create", description = "Save any file attaching to loan", operationId = "file-create")
    public ResponseEntity<ResultData<FileBaseResponseDTO>> create(@RequestPart("file") MultipartFile file,
                                                                  @RequestPart("body") @Valid FileCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(
                fileService.save(file, requestDTO)
        );
    }

    @GetMapping("/byLoan")
    @Operation(summary = "Find Files list", description = "Find loan files list", operationId = "file-find-byLoan")
    public ResponseEntity<ResultData<List<FileBaseResponseDTO>>> findFilesByLoan(@RequestParam("loanExtId") String loanExtId) {
        return ResponseEntity.ok(fileService.findFileListByLoan(loanExtId));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "File Download", description = "Download file by id", operationId = "file-download-byId")
    public ResponseEntity<InputStreamResource> getById(@PathVariable Long id) {
        var file = fileService.getFileById(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(file.getResult().getContentType()))
                .header("Content-disposition", "attachment; filename=" + file.getResult().getFileName())
                .body(file.getResult().getInputStreamResource());
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "File delete", description = "Delete file by id", operationId = "file-delete-byId")
    public ResponseEntity<ResultData<FileBaseResponseDTO>> deleteById(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.deleteFileById(id));
    }

    @PostMapping(value = "/save/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Files save service", description = "Save any file attaching to loan", operationId = "file-save-multiple")
    public ResponseEntity<ResultData<List<FileSaveResponseDTO>>> create(@RequestPart("files") MultipartFile[] files,
                                                                        @RequestPart("loan_ext_id") @Valid @LoanId String loanExtId) {
        return ResponseEntity.ok(
                fileService.saveV2(files, loanExtId)
        );
    }
}
