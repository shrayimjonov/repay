package uz.nodir.client.controller.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uz.nodir.client.model.dto.info.response.GCPDocumentResponseDTO
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.enums.GCPDocument


@RestController
@RequestMapping("/api/v1/info", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "info-endpoint")
class InfoWebController {

    @GetMapping("/gcp/docType")
    @Operation(description = "Get document types from GCP", operationId = "info-gcp-document")
    fun gcpDocType(): ResponseEntity<ResultData<List<GCPDocumentResponseDTO>>> =
        ResponseEntity.ok(
            ResultData.ok(
                GCPDocument.entries
                    .map(::GCPDocumentResponseDTO)
            )
        )


}