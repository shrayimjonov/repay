package uz.nodir.processing.service.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import uz.nodir.processing.model.entity.Card
import uz.nodir.processing.repository.CardRepository
import uz.nodir.processing.service.dao.CardDAO


@Service
class CardDAOImpl(
    private val repository: CardRepository
) : CardDAO {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    override fun findById(id: Long): Mono<Card?> {
        log.info("Find card by id: {}", id)
        val result = repository.findById(id)

        log.info("Find result is: {}", result)
        return result
    }
}