package uz.nodir.user.service.core.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import uz.nodir.user.model.entity.TrustedUser
import uz.nodir.user.repository.TrustedUserRepository
import uz.nodir.user.service.core.dao.TrustedUserDAO
import kotlin.jvm.optionals.getOrNull


@Service
class TrustedUserDAOImpl(
    private val repository: TrustedUserRepository
) : TrustedUserDAO {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun findByUsername(specification: Specification<TrustedUser>, pageable: Pageable): Page<TrustedUser> {
        log.info("Find trusted user by specification")

        return repository.findAll(specification, pageable)
            .also { log.info("Found trusted user result with size: ${it.size}") }
    }

    override fun findById(id: Long): TrustedUser? {
        log.info("Find trusted user by id: $id")

        return repository.findById(id)
            .getOrNull()
            ?.also {
                log.info("Found trusted user by id: $id")
            } ?: run {
            log.info("Trusted user by id: $id not found!")
            null
        }
    }

    override fun deleteById(id: Long) {
        log.info("Delete trusted user by id: $id")
        return repository.deleteById(id)
    }


}