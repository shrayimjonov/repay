package uz.nodir.logprotocol.service.core.dao

import uz.nodir.logprotocol.model.domain.LogProtocol

interface LogProtocolDAO {

    fun save(logProtocol: LogProtocol): LogProtocol
}