package uz.nodir.court.service.core.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uz.nodir.court.model.entity.Court
import uz.nodir.court.repository.CourtRepository
import uz.nodir.court.service.core.dao.CourtDAO
import java.util.UUID


@Service
class CourtDAOImpl(
    private val repository: CourtRepository
) : CourtDAO {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional
    override fun save(court: Court): Court {
        log.info("Save/Update court entity: {}", court)
        val result = repository.save(court)

        log.info("Saved court entity id: {}", result.id)
        return result
    }

    @Transactional(readOnly = true)
    override fun findAllByLoanExtId(loanExtId: String, pageable: Pageable): Page<Court> {
        log.info("Find courts by loanExtId: $loanExtId")
        val result = repository.findAllByLoanExtID(loanExtId, pageable)

        log.info("Found {} courts", result.content.size)
        return result
    }

    @Transactional(readOnly = true)
    override fun findByJobId(jobId: UUID): Court? {
        log.info("Find court by jobId: $jobId")
        val court = repository.findCourtByJobId(jobId)

        court?.also {
            log.info("Found court: {}", court)
        } ?: {
            log.info("Court not fund")
        }
        return court
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): Court? {
        log.info("Find court by id: $id")
        val courtOptional = repository.findById(id)

        if (courtOptional.isPresent) {
            val court = courtOptional.get()
            log.info("Found court: {}", court)
            return court
        }

        log.info("Court not fund")
        return null
    }

    override fun deleteById(id: Long) {
        log.info("Delete court by id: $id")
        repository.deleteById(id)
    }
}