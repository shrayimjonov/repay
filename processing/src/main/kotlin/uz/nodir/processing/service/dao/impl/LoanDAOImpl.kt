package uz.nodir.processing.service.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uz.nodir.processing.model.entity.Loan
import uz.nodir.processing.repository.LoanRepository
import uz.nodir.processing.service.dao.LoanDAO


@Service
class LoanDAOImpl(
    private val repository: LoanRepository
): LoanDAO {
    companion object{
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    override fun findById(id: Long): Mono<Loan?> {
        log.info("Find loan by id: $id")
        val loan = repository.findById(id)

        log.info("Found loan: $loan")
        return loan

    }
}