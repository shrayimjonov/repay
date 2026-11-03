package uz.nodir.processing.model.entity

import jakarta.persistence.*
import uz.nodir.processing.model.entity.base.BaseCardEntity
import uz.nodir.processing.model.entity.pk.LoanCardKey
import java.io.Serializable


@Entity
@Table(name = "loan_cards")
class LoanCard (
    @EmbeddedId
    val id: LoanCardKey,

    @ManyToOne
    @MapsId("loanId")
    @JoinColumn(name = "loan_id")
    val loan: Loan

): BaseCardEntity(), Serializable {
    companion object {
        private const val SERIAL_VERSION_UID: Long = 1234567L
    }
}