package uz.nodir.court.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uz.nodir.court.model.entity.ChambersApplication
import java.util.*


@Repository
interface ChambersRepository: JpaRepository<ChambersApplication, UUID> {

    fun findByLoanExtId(loanExtId: String): List<ChambersApplication>

    fun findByAppId(chamberId: String): Optional<ChambersApplication>
}