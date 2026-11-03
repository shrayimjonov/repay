package uz.nodir.court.service.core.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uz.nodir.court.model.entity.ChambersApplication
import uz.nodir.court.repository.ChambersRepository
import uz.nodir.court.service.core.dao.ChambersApplicationDAO
import java.util.*


@Service
class ChambersApplicationDAOImpl(
    private val repository: ChambersRepository
) : ChambersApplicationDAO {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional
    override fun save(chambersApplication: ChambersApplication): ChambersApplication {
        log.info("Save/update chambers entity: $chambersApplication")
        val entity = repository.save(chambersApplication)

        log.info("Saved entity id: ${entity.id}")
        return entity
    }

    @Transactional(readOnly = true)
    override fun findByLoanExtId(loanExtId: String): List<ChambersApplication> {
        log.info("Find chambers by loanExtId: $loanExtId")
        val result = repository.findByLoanExtId(loanExtId)

        log.info("Found ${result.size} chambers applications")
        return result
    }

    @Transactional(readOnly = true)
    override fun findByChamberId(chamberId: String): ChambersApplication? {
        log.info("Find chambers by id: $chamberId")
        val result = repository.findByAppId(chamberId)

        log.info("Chamber result is present: ${result.isPresent}")
        return result.orElse(null)
    }

}