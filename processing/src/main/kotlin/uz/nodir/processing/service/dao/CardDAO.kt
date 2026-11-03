package uz.nodir.processing.service.dao

import reactor.core.publisher.Mono
import uz.nodir.processing.model.entity.Card

interface CardDAO {

    fun findById(id: Long): Mono<Card?>
}