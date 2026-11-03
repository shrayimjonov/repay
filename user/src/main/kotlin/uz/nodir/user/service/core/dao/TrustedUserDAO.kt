package uz.nodir.user.service.core.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import uz.nodir.user.model.entity.TrustedUser

interface TrustedUserDAO {

    fun findByUsername(specification: Specification<TrustedUser>, pageable: Pageable): Page<TrustedUser>

    fun findById(id: Long): TrustedUser?

    fun deleteById(id: Long)
}