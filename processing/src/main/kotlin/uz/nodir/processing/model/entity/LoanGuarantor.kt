package uz.nodir.processing.model.entity

import jakarta.persistence.*
import uz.nodir.processing.model.entity.pk.LoanGuarantorKey
import uz.repay.common.model.entity.BaseEntity


@Entity
@Table(name = "loan_guarantors")
class LoanGuarantor(
    @EmbeddedId
    val id: LoanGuarantorKey,

    @ManyToOne @MapsId("loanId") @JoinColumn(name = "loan_id")
    val loan: Loan,

    @ManyToOne
    @MapsId("guarantorId")
    @JoinColumn(name = "guarantor_id")
    val guarantor: Guarantor,

    @Column(name = "is_deleted")
    var isDeleted: Boolean? = null,

    @Column(name = "ip_address")
    var ipAddress: String? = null


) : BaseEntity()