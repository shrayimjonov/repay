package uz.nodir.processing.model.entity

import jakarta.persistence.*
import uz.nodir.processing.model.entity.base.BaseCardEntity
import uz.nodir.processing.model.entity.pk.GuarantorCardKey
import java.io.Serializable

@Entity
@Table(name = "guarantor_cards")
class GuarantorCard(
    @EmbeddedId
    val id: GuarantorCardKey,

    @ManyToOne
    @MapsId("guarantorId")
    @JoinColumn(name = "guarantor_id")
    val guarantor: Guarantor

) : BaseCardEntity(), Serializable {
    companion object {
        private const val SERIAL_VERSION_UID: Long = 1234567L
    }
}