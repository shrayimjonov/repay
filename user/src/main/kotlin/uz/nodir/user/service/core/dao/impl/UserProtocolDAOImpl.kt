package uz.nodir.user.service.core.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import uz.nodir.user.model.entity.TrustedUser
import uz.nodir.user.model.entity.UserProtocol
import uz.nodir.user.repository.UserProtocolRepository
import uz.nodir.user.service.core.dao.UserProtocolDAO


@Service
class UserProtocolDAOImpl(
    private val repository: UserProtocolRepository
): UserProtocolDAO {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun findByUsername(specification: Specification<UserProtocol>, pageable: Pageable): Page<UserProtocol> {
        log.info("Find user protocol by specification")

        return repository.findAll(specification, pageable)
            .also { log.info("Found user protocol result with size: ${it.size}") }
    }

}