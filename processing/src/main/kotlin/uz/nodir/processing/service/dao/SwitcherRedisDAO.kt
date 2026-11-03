package uz.nodir.processing.service.dao

import reactor.core.publisher.Mono
import uz.nodir.processing.redis.entity.Switcher

interface SwitcherRedisDAO {

    fun findByName(switcher: String): Mono<Switcher?>
}