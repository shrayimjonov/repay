package uz.nodir.processing.model.entity.pk

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable


@Embeddable
data class GuarantorCardKey(

    @Column(name = "loan_id")
    val loanId: Long? = null,

    @Column(name = "guarantor_id")
    val guarantorId: Long? = null

) : Serializable {
    companion object {
        private const val SERIAL_VERSION_UID: Long = 1234567L
    }

}