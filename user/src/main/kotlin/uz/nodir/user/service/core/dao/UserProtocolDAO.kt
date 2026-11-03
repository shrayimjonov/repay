package uz.nodir.user.service.core.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import uz.nodir.user.model.entity.UserProtocol

interface UserProtocolDAO {

    fun findByUsername(specification: Specification<UserProtocol>, pageable: Pageable): Page<UserProtocol>
}