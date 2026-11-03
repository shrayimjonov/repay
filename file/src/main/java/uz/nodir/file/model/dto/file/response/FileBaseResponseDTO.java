package uz.nodir.file.model.dto.file.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.file.model.entity.FileSaver;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileBaseResponseDTO {
    @JsonProperty("id")
    @Schema(name = "id", example = "544", description = "id field")
    private Long id;
    @Schema(
            name = "loanExtId",
            example = "01121158076",
            description = "loan external id"
    )
    private String loanExtId;
    @JsonProperty("name")
    @Schema(name = "name", example = "01086239453c04978b-a41d-45ed-ac22-5326aa46b134IVANOV IVAN_01.jpg", description = "file name")
    private String name;
    @JsonProperty("contentType")
    @Schema(name = "contentType", example = "application/pdf", description = "content type of file")
    private String contentType;
    @JsonProperty("description")
    @Schema(name = "description", example = "Shartnomasini to'liq varianti", description = "file description")
    private String description;

    public FileBaseResponseDTO(FileSaver fileSaver) {
        this.id = fileSaver.getId();
        this.loanExtId = fileSaver.getLoanExtId();
        this.name = fileSaver.getName();
        this.contentType = fileSaver.getContentType();
        this.description = fileSaver.getDescription();
    }
}
