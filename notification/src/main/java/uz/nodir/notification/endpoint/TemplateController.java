package uz.nodir.notification.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.nodir.notification.model.dto.template.request.TemplateBaseRequestDTO;
import uz.nodir.notification.model.dto.template.request.TemplateFilterRequestDTO;
import uz.nodir.notification.model.dto.template.response.TemplateBaseResponseDTO;
import uz.nodir.notification.model.dto.template.response.TemplateFilterResponseDTO;
import uz.nodir.notification.service.business.TemplateService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;

@RestController
@RequestMapping(value = "/api/v1/template", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "template-methods")
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping
    @Operation(summary = "Create template", description = "Create new template method", operationId = "template-create")
    public ResponseEntity<ResultData<TemplateBaseResponseDTO>> create(@RequestBody @Valid TemplateBaseRequestDTO requestDTO) {
        return ResponseEntity.ok(templateService.create(requestDTO));
    }

    @PatchMapping("/evict")
    @Operation(summary = "Evict template cache", description = "Evict template cache method", operationId = "template-evict-cache")
    public ResponseEntity<ResultData<CoreError>> evict() {
        return ResponseEntity.ok(templateService.evict());
    }

    @PostMapping("/filter")
    @Operation(summary = "Get templates with filter", description = "Get templates list by filter", operationId = "template-filter-find")
    public ResponseEntity<ResultData<TemplateFilterResponseDTO>> filter(@RequestBody @Valid TemplateFilterRequestDTO requestDTO) {
        return ResponseEntity.ok(templateService.filter(requestDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by id", description = "Get template info by id", operationId = "template-find-byId")
    public ResponseEntity<ResultData<TemplateBaseResponseDTO>> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(templateService.findById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template by id", description = "Delete template by id", operationId = "template-delete-byId")
    public ResponseEntity<ResultData<CoreError>> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(templateService.deleteById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update template", description = "Update template by id", operationId = "template-update-byId")
    public ResponseEntity<ResultData<CoreError>> updateById(
            @PathVariable("id") Long id,
            @RequestBody @Valid TemplateBaseRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(templateService.update(id, requestDTO));
    }


}
