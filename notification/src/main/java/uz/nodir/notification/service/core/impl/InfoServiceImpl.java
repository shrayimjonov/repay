package uz.nodir.notification.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.notification.model.dto.core.response.TemplateTypeResponseDTO;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.service.core.InfoService;
import uz.nodir.notification.service.core.TemplateDAO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final TemplateDAO templateDAO;

    @Override
    public ResultData<List<TemplateTypeResponseDTO>> takeTemplateTypes() {
        return ResultData.ok(
                Arrays.stream(
                                TemplateType.values())
                        .map(z -> {
                                    var template = findByType(z);

                                    return new TemplateTypeResponseDTO(z, template);
                                }
                        )
                        .toList()
        );
    }

    private Template findByType(TemplateType type) {
        return templateDAO
                .findAll()
                .stream()
                .filter(z -> z.getSmsType().equals(type))
                .findFirst()
                .orElse(null);
    }
}
