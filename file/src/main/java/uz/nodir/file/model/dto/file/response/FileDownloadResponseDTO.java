package uz.nodir.file.model.dto.file.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadResponseDTO {
    private InputStreamResource inputStreamResource;
    private String contentType;
    private String fileName;
}
