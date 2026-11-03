package uz.nodir.processing.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import uz.nodir.processing.model.entity.Card


@Repository
interface CardRepository : R2dbcRepository<Card, Long>