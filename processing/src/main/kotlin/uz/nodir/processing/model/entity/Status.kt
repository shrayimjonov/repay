package uz.nodir.processing.model.entity

import jakarta.persistence.*
import uz.repay.common.model.entity.BaseEntity
import uz.repay.common.model.enums.Processing
import uz.repay.common.model.enums.StatusType
import java.io.Serializable


@Entity
@Table(name = "status")
class Status(
    @SequenceGenerator(name = "card_stat_seq", sequenceName = "card_stat_seq", allocationSize = 1)
    @GeneratedValue(
        generator = "card_stat_seq",
        strategy = GenerationType.SEQUENCE
    )
    @Id
    @org.springframework.data.annotation.Id
    val id: Long? = null,

    @Column(name = "code", nullable = false)
    private var code: String,

    @Column(name = "description")
    val definition: String? = null,

    @Column(name = "description_raw")
    val definitionUz: String? = null,

    @Column(name = "processing")
    @Enumerated(EnumType.STRING)
    val processing: Processing? = null,

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    val type: StatusType? = null

) : BaseEntity(), Serializable {
    companion object {
        private const val SERIAL_VERSION_UID: Long = 1234567L
    }
}