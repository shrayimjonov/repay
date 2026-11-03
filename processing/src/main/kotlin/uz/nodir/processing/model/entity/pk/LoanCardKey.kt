package uz.nodir.processing.model.entity.pk

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable


@Embeddable
data class LoanCardKey(

    @Column(name = "loan_id")
    val loanId: Long,

    @Column(name = "card_id")
    val cardId: Long,
) : Serializable {
    companion object {
        private const val SERIAL_VERSION_UID: Long = 1234567L
    }

}