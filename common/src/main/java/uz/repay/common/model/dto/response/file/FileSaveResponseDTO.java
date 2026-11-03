package uz.repay.common.model.dto.response.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileSaveResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("base_64")
    @ToString.Exclude
    @Transient
    private String base64;

    @JsonProperty("extension")
    private String extension;

}
