package uz.nodir.file.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.io.IOException;
import java.util.Base64;


@Slf4j
public class FileUtils {

    public static SimpleResponse<String> getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            var extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            return new SimpleResponse<>(extension);
        }
        log.error("File extension not supported: {}", originalFilename);
        return new SimpleResponse<>(ResultState.WrongParams.getCode(), "File extension not supported");
    }

    public static SimpleResponse<String> convertToBase64(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            var base64Str = Base64.getEncoder().encodeToString(fileBytes);
            return new SimpleResponse<>(base64Str);
        } catch (IOException i) {
            log.error("File: {} can't convert to base64. Cause: {}", file.getOriginalFilename(), i.getMessage());
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), i.getMessage());
        }
    }
}
