package uz.nodir.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import uz.nodir.user.model.entity.TrustedUser


@Repository
interface TrustedUserRepository: JpaRepository<TrustedUser, Long>, JpaSpecificationExecutor<TrustedUser> {


}