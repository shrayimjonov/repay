package uz.repay.report.model.dto.report.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanGraphResponseDTO {
    @JsonProperty("file")
    @Schema(name = "file", description = "file in base 64 format")
    private String file;
    @JsonProperty("name")
    @Schema(name = "name", description = "file name for saving", example = "01121158076_cb81f3a3-cdd8-4577-8e91-9684bc1e13be.pdf")
    private String name;
}
