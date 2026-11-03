package uz.nodir.processing.model.entity.base

import jakarta.persistence.*
import uz.nodir.processing.model.entity.Card
import uz.repay.common.model.entity.BaseEntity
import uz.repay.common.model.enums.AddType
import uz.repay.common.model.enums.Payer
import java.io.Serializable


@MappedSuperclass
data class BaseCardEntity(
    @ManyToOne @MapsId("cardId") @JoinColumn(name = "card_id")
    val card: Card? = null,

    @Column(name = "payer")
    @Enumerated(EnumType.STRING)
    val payer: Payer? = null,

    @Column(name = "is_deleted")
    val isDeleted: Boolean? = null,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(name = "added_type")
    @Enumerated(EnumType.STRING)
    val addType: AddType? = null,

    @Column(name = "is_confirmed")
    val isConfirmed: Boolean? = null,

) : BaseEntity(), Serializable {
    companion object {
        private const val SERIAL_VERSION_UID: Long = 1234567L
    }
}