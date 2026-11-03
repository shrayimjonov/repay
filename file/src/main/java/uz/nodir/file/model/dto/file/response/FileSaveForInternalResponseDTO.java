package uz.nodir.file.model.dto.file.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileSaveForInternalResponseDTO {

    @JsonProperty("ids")
    private List<Long> idList;
}
