package uz.nodir.logprotocol.service.core.dao.impl

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uz.nodir.logprotocol.model.domain.LogProtocol
import uz.nodir.logprotocol.repository.LogProtocolRepository
import uz.nodir.logprotocol.service.core.dao.LogProtocolDAO

@Service
@Slf4j
class LogProtocolDAOImpl @Autowired constructor(
    private val repository: LogProtocolRepository
) : LogProtocolDAO {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun save(logProtocol: LogProtocol): LogProtocol {
        log.info("Save/update log protocol entity: {}", logProtocol)
        val entity = repository.save(logProtocol)
        log.info("Saved entity id: {}", entity.id);
        return entity
    }

}