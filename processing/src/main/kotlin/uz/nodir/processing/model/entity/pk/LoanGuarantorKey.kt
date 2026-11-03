package uz.nodir.processing.model.entity.pk

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable


@Embeddable
data class LoanGuarantorKey(

    @Column(name = "guarantor_id")
    val guarantorId: Long? = null,


    @Column(name = "card_id")
    val cardId: Long? = null

) : Serializable {
    companion object {
        private const val SERIAL_VERSION_UID: Long = 1234567L
    }

}