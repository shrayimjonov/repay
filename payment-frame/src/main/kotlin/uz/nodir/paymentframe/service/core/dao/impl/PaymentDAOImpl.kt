package uz.nodir.paymentframe.service.core.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.paymentframe.redis.entity.Payment
import uz.nodir.paymentframe.redis.repository.PaymentRepository
import uz.nodir.paymentframe.service.core.dao.PaymentDAO
import java.util.*
import kotlin.jvm.optionals.getOrNull


@Service
class PaymentDAOImpl(
    private val repository: PaymentRepository
) : PaymentDAO {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun save(payment: Payment): Payment {
        log.info("Save/update payment entity: $payment")
        return repository.save(payment)
            .also {
                log.info("Saved payment entity id: ${it.id}")
            }
    }

    override fun findById(id: UUID): Payment? {
        log.info("Find entity by id: $id")

        return repository.findById(id)
            .getOrNull()
            ?.also {
                log.info("Found entity by id: $it")
            } ?: run {
            log.info("Payment entity not found by id: $id")
            null
        }
    }


}