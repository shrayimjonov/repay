package uz.nodir.file.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.nodir.file.model.dto.file.request.FileCreateRequestDTO;
import uz.nodir.file.model.dto.file.response.FileBaseResponseDTO;
import uz.nodir.file.model.dto.file.response.FileDownloadResponseDTO;
import uz.nodir.file.model.entity.FileSaver;
import uz.nodir.file.model.property.FileProperty;
import uz.nodir.file.service.business.FileSenderService;
import uz.nodir.file.service.business.FileService;
import uz.nodir.file.service.core.FileSaverDAO;
import uz.nodir.file.utils.FileUtils;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.file.FileSaveResponseDTO;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileSenderService fileSenderService;
    private final FileSaverDAO fileSaverDAO;
    private final FileProperty fileProperty;
    private final static String FILE_NAME_BASIC = "INTERNAL_FILE_SAVER";

    @Override
    public ResultData<FileBaseResponseDTO> save(@NotNull MultipartFile file,
                                                @NotNull FileCreateRequestDTO requestDTO) {
        log.info("Received file : {} to save loan: {}", file.getOriginalFilename(), requestDTO.getLoanExtId());

        validateRequest(file, requestDTO);

        var savedFile = handleFileSaving(file, requestDTO.getLoanExtId(), requestDTO.getDescription(), false);


        return ResultData.ok(
                new FileBaseResponseDTO(
                        savedFile
                )
        );
    }

    private FileSaver handleFileSaving(MultipartFile file, String loanExtId, String description, boolean isInternal) {
        var fileName = fileNameBuilder(file.getOriginalFilename(), loanExtId);

        fileSenderService.save(
                file,
                fileName
        );

        var preparedFile = new FileSaver(
                loanExtId,
                fileName,
                file.getContentType(),
                description,
                isInternal
        );

        return fileSaverDAO.save(preparedFile);
    }

    private void validateRequest(MultipartFile file, FileCreateRequestDTO requestDTO) {
        checkUploadFileSize(file);

        checkUploadFilesCount(requestDTO);
    }

    private void checkUploadFileSize(MultipartFile file) {
        if (file.getSize() / 1_000_000D > fileProperty.getMaxFileSizeInMB())
            throw new InvalidDataException("File: %s size: %f more than waiting: %d"
                    .formatted(file.getOriginalFilename(), file.getSize() / 1_000_000D, fileProperty.getMaxFileSizeInMB()));
    }

    private SimpleResponse<CoreError> checkUploadFilesSize(MultipartFile[] files) {

        var totalFilesSize = Arrays.stream(files)
                .mapToLong(z -> z.getSize() / 1_000_000)
                .sum();

        if (totalFilesSize > fileProperty.getChambersFilesSizeInMB()) {
            var message = "Files size: %d more than waiting: %d".formatted(totalFilesSize, fileProperty.getChambersFilesSizeInMB());
            log.error(message);
            return new SimpleResponse<>(ResultState.InvalidRequest.getCode(), message);
        }

        return new SimpleResponse<>(
                new CoreError(
                        0,
                        ResponseStatus.SUCCESS.name())
        );
    }


    private void checkUploadFilesCount(FileCreateRequestDTO requestDTO) {
        var filesList = fileSaverDAO.findFilesByLoanExtId(requestDTO.getLoanExtId());

        if (filesList.size() == fileProperty.getMaxCountForOneLoan() || filesList.size() > fileProperty.getMaxCountForOneLoan())
            throw new InvalidDataException("You cannot upload more than 10 files. Loan: %s".formatted(requestDTO.getLoanExtId()));
    }


    private static String fileNameBuilder(String fileOriginalName, String loanExtId) {
        var fileName = TextUtils.cyrillicToLatin(fileOriginalName);

        var lastname = fileName.length() >= 250 ? fileName.substring(fileName.length()-250) : fileName;

        var preparedFileName = loanExtId + UUID.randomUUID() + lastname;

        log.info("Prepared filename for bucket: {}", preparedFileName);

        return preparedFileName;
    }

    @Override
    public ResultData<List<FileBaseResponseDTO>> findFileListByLoan(@NotNull String loanExtId) {
        var files = fileSaverDAO.findFilesByLoanExtId(loanExtId);

        return ResultData.ok(
                files
                        .stream()
                        .map(FileBaseResponseDTO::new)
                        .toList()
        );
    }

    @Override
    public ResultData<FileDownloadResponseDTO> getFileById(@NotNull Long id) {
        var fileSaver = fileSaverDAO.findById(id);
        log.info("Download file : {}", fileSaver);

        InputStreamResource file = fileSenderService.getByName(fileSaver.getName());
        return ResultData.ok(
                new FileDownloadResponseDTO(
                        file,
                        fileSaver.getContentType(),
                        fileSaver.getName()
                )
        );
    }

    @Override
    public ResultData<FileBaseResponseDTO> deleteFileById(@NotNull Long id) {
        log.info("Received command to delete file by id: {}", id);
        var file = fileSaverDAO.findById(id);

        fileSenderService.deleteByName(file.getName());

        var response = ResultData.ok(
                new FileBaseResponseDTO(file)
        );

        log.info("Finished command to delete file by id with response: {}", response);
        return response;
    }

    @Override
    public ResultData<List<FileSaveResponseDTO>> saveV2(@NotNull MultipartFile[] files,
                                                        @NotNull String loanExtId) {
        log.info("Received command to save files from internal by loan: {}", loanExtId);

        var sizeValidationResult = checkUploadFilesSize(files);

        if (!sizeValidationResult.getStatus())
            return new ResultData<>(sizeValidationResult.getError(), false);

        var fileList = Arrays.stream(files)
                .map(f -> {
                            var fileExtensionResult = FileUtils.getFileExtension(f);

                            if (!fileExtensionResult.getStatus())
                                throw new InvalidDataException(fileExtensionResult.getError().getMessage());

                            var fileId = handleFileSaving(f, loanExtId, FILE_NAME_BASIC, true).getId();

                            var fileBase64Result = FileUtils.convertToBase64(f);

                            if (!fileBase64Result.getStatus())
                                throw new InvalidDataException(fileBase64Result.getError().getMessage());

                            return new FileSaveResponseDTO(fileId, fileBase64Result.getData(), fileExtensionResult.getData());
                        }
                )
                .toList();

        var responseDTO = ResultData.ok(
                fileList
        );

        log.info("Finished command to save files with response: {}", responseDTO);
        return responseDTO;
    }
}
