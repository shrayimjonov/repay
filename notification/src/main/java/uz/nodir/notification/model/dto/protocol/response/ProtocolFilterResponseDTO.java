package uz.nodir.notification.model.dto.protocol.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.nodir.notification.model.dto.protocol.response.result.ProtocolBaseResult;
import uz.nodir.notification.model.entity.SMSProtocol;
import uz.repay.common.model.dto.response.core.result.PageResult;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProtocolFilterResponseDTO extends PageResult {
    private List<ProtocolBaseResult> list;

    public ProtocolFilterResponseDTO(Page<SMSProtocol> page) {
        super(page.getSize(),
                page.getNumber() + 1,
                page.getNumberOfElements(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.isFirst());

        this.list = page
                .getContent()
                .stream()
                .map(ProtocolBaseResult::new)
                .toList();
    }
}
