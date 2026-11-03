package uz.repay.loan.model.dto.core.note.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.loan.model.entity.Note;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteBaseResponseDTO {
//    @JsonProperty("loan_ext_id")
//    @Schema(name = "loanExtId", description = "loan external id in ABS", example = "0098127386", pattern = "\\d{9,}")
//    private String loanExtId;

    @JsonProperty("note_id")
    @Schema(name = "note_id", description = "note id", example = "5")
    private Long id;

    @JsonProperty("note")
    @Schema(name = "note", description = "note to loan", example = "This debtor is always paying late")
    private String note;

    @JsonProperty("created_by")
    @Schema(name = "created_by", description = "Employee who added this note", example = "user@example.com")
    private String createdBy;

    @JsonProperty("created_time")
    @Schema(name = "created_time", description = "Date time when note created", example = "2024-05-21 10:56:42.424")
    private LocalDateTime createdTime;

    public NoteBaseResponseDTO(Note note) {
        this.id = note.getId();
        this.note = note.getText();
        this.createdBy = note.getCreatedBy();
        this.createdTime = note.getInitTime();
    }
}
