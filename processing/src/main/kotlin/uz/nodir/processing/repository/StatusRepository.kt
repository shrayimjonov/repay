package uz.nodir.processing.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import uz.nodir.processing.model.entity.Status

@Repository
interface StatusRepository : R2dbcRepository<Status, Long>