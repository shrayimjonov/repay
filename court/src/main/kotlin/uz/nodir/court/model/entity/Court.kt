package uz.nodir.court.model.entity

import jakarta.persistence.*
import uz.repay.common.model.entity.BaseEntity
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(
    name = "court",
    indexes = [
        Index(
            name = "court_loanExtId_ind",
            columnList = "loan_ext_id"
        )
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "court_loanExtId_executeDate_un",
            columnNames = ["loan_ext_id", "execute_date"]
        )
    ]
)
data class Court(
    @SequenceGenerator(
        name = "court_seq",
        sequenceName = "court_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        generator = "court_seq",
        strategy = GenerationType.SEQUENCE
    )
    @Id
    val id: Long? = null,

    @Column(name = "loan_ext_id")
    val loanExtID: String? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "message")
    var message: String? = null,

    @Column(name = "execute_date")
    var executeDate: LocalDateTime? = null,

    @Column(name = "job_id")
    val jobId: UUID? = null,

    ) : BaseEntity()
