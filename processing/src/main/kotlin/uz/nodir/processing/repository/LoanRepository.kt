package uz.nodir.processing.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import uz.nodir.processing.model.entity.Loan


@Repository
interface LoanRepository : R2dbcRepository<Loan, Long> {
    fun findByExtId(loanExtId: String): Mono<Loan?>
}