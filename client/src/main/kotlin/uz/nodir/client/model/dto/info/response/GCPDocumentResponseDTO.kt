package uz.nodir.client.model.dto.info.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import uz.repay.common.model.enums.GCPDocument

data class GCPDocumentResponseDTO(
    @get:Schema(
        name = "description",
        example = "Внешний паспорт",
        description = "Document description"
    )
    @JsonProperty(value = "description")
    var description: String? = null,

    @get:Schema(
        name = "document",
        example = "IDMS_RECV_IP_DOCUMENTS",
        description = "Document type"
    )
    @JsonProperty(value = "document")
    var document: GCPDocument? = null
) {

    constructor(document: GCPDocument) : this() {
        this.description = document.description
        this.document = document
    }
}

