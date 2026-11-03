package uz.nodir.processing.service.dao

import reactor.core.publisher.Mono
import uz.nodir.processing.model.entity.Loan

interface LoanDAO {
    fun findById(id: Long): Mono<Loan?>
}