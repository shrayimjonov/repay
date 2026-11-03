package uz.nodir.logprotocol.model.domain

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import uz.repay.common.model.enums.ProtocolType
import uz.repay.common.utils.DateUtils
import java.time.LocalDateTime



@Entity
@Table(name = "log_protocol")
class LogProtocol(
    @SequenceGenerator(name = "log_protocol_seq", sequenceName = "log_protocol_seq", allocationSize = 1)
    @GeneratedValue(generator = "log_protocol_seq", strategy = GenerationType.SEQUENCE)
    @Id
    var id: Long? = null,

    @Column(name = "loan_ext_id")
    val loanExtId: String? = null,

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    var type: ProtocolType? = null,

    @Column(name = "request")
    @JdbcTypeCode(SqlTypes.JSON)
    var request: Map<Any, Any>? = null,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(name = "created_time", updatable = false)
    val createdTime: LocalDateTime? = DateUtils.getDateTimeNow(),

    @Column(name = "created_by", updatable = false)
    val createdBy: String? = null

)