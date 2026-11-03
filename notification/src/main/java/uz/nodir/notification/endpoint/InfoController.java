package uz.nodir.notification.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.nodir.notification.model.dto.core.response.TemplateTypeResponseDTO;
import uz.nodir.notification.model.dto.core.response.ValueTypeResponseDTO;
import uz.nodir.notification.model.enums.ValueType;
import uz.nodir.notification.service.core.InfoService;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/info")
@RequiredArgsConstructor
@Tag(name = "info-methods")
public class InfoController {
    private final InfoService infoService;

    @GetMapping("/template")
    @Operation(summary = "Template types", description = "All of template types", operationId = "info-template-type")
    public ResponseEntity<ResultData<List<TemplateTypeResponseDTO>>> templateTypes() {
        return ResponseEntity.ok(infoService.takeTemplateTypes());
    }

    @GetMapping("/valueType")
    @Operation(summary = "Value types", description = "All of value types", operationId = "info-value-type")
    public ResponseEntity<ResultData<List<ValueTypeResponseDTO>>> valueTypes() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(ValueType.values())
                                .map(ValueTypeResponseDTO::new)
                                .toList()
                )
        );
    }


}
