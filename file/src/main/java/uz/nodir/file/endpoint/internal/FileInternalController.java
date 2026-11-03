package uz.nodir.file.endpoint.internal;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.nodir.file.service.business.FileService;
import uz.repay.common.model.dto.response.annotations.LoanId;


@RestController
@RequestMapping("/api/v1/internal/file")
@RequiredArgsConstructor
@Hidden
public class FileInternalController {
    private final FileService fileService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestPart("files") MultipartFile[] files,
                                                         @RequestPart("loan_ext_id") @Valid @LoanId String loanExtId) {
        return ResponseEntity.ok(
                fileService.saveV2(files, loanExtId)
        );
    }


}
