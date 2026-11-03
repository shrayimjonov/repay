package uz.nodir.file.model.dto.location.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.nodir.file.model.entity.Location;
import uz.repay.common.model.dto.response.core.result.PageResult;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LocationFilterResponseDTO extends PageResult {
    private List<LocationBaseResponseDTO> data;

    public LocationFilterResponseDTO(Page<Location> pageResult) {
        super(pageResult.getSize(),
                pageResult.getNumber() + 1,
                pageResult.getNumberOfElements(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.isLast(),
                pageResult.isFirst());

        this.data = pageResult
                .getContent()
                .stream()
                .map(LocationBaseResponseDTO::new)
                .toList();
    }

}
