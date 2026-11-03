package uz.nodir.court.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import uz.nodir.court.model.dto.application.request.ApplicationCreateRequestDTO
import uz.repay.common.model.entity.BaseEntity
import uz.repay.common.model.enums.ResponseStatus
import uz.repay.common.utils.HttpServletUtils
import java.util.*


@Entity
@Table(
    name = "chambers_application",
    indexes = [
        Index(columnList = "loanExtId"),
        Index(columnList = "createdDate"),
    ]
)
data class ChambersApplication(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(name = "ext_id")
    var extId: String? = null,

    @Column(name = "loan_ext_id", nullable = false)
    val loanExtId: String? = null,

    @Column(name = "ip_address", updatable = false)
    val ipAddress: String? = null,

    @Column(name = "request")
    @JdbcTypeCode(value = SqlTypes.JSON)
    var request: Map<Any, Any>? = null,

    @Column(name = "response")
    @JdbcTypeCode(value = SqlTypes.JSON)
    var response: Map<Any, Any>? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: ResponseStatus? = null,

    @Column(name = "file_ids", columnDefinition = "jsonb")
    @JdbcTypeCode(value = SqlTypes.JSON)
    var fileIds: List<Long>? = listOf(),

    @Column(name = "responsible_person")
    val responsiblePerson: String? = null,

    @Column(name = "app_id")
    var appId: String? = null,

    @Column(name = "note")
    var note: String? = null,


) : BaseEntity() {

    companion object {
        fun from(requestDTO: ApplicationCreateRequestDTO): ChambersApplication {

            return ChambersApplication(
                loanExtId = requestDTO.loanExtId,
                ipAddress = HttpServletUtils.getRequestIPAddress(),
                request = requestDTO.toMap(),
                status = ResponseStatus.CREATED,
                responsiblePerson = requestDTO.document.responsiblePerson,
                fileIds = requestDTO.document.fileParams?.map { s -> s.id } ?: listOf(),
            )
        }
    }
}