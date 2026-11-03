package uz.nodir.court.service.core.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import uz.nodir.court.model.entity.Court
import java.util.*

interface CourtDAO {
    fun save(court: Court): Court

    fun findAllByLoanExtId(loanExtId: String, pageable: Pageable): Page<Court>

    fun findByJobId(jobId: UUID): Court?

    fun findById(id: Long): Court?

    fun deleteById(id: Long)
}