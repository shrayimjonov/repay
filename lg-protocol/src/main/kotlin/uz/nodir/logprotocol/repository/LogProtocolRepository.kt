package uz.nodir.logprotocol.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uz.nodir.logprotocol.model.domain.LogProtocol

@Repository
interface LogProtocolRepository : JpaRepository<LogProtocol, Long> {

    fun findAllByLoanExtId(loanExtId: String) : List<LogProtocol>

}