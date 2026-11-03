package uz.repay.loan.model.dto.core.note.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteUpdateRequestDTO {
    @JsonProperty("note_id")
    @Schema(name = "note_id", description = "note id", example = "5")
    @NotNull
    @Min(1)
    private Long id = 0L;

    @JsonProperty("note")
    @NotBlank
    @Schema(name = "note", description = "note to loan", example = "This debtor is always paying late")
    private String note;
}
