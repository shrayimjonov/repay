package uz.nodir.court.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uz.nodir.court.model.entity.Court
import java.util.*

@Repository
interface CourtRepository : JpaRepository<Court, Long> {

    fun findAllByLoanExtID(loanExtId: String, pageable: Pageable) : Page<Court>

    fun findCourtByJobId(jobId: UUID) : Court?
}