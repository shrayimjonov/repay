package uz.nodir.processing.service.dao.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import uz.nodir.processing.redis.entity.Switcher
import uz.nodir.processing.redis.repository.SwitcherRepository
import uz.nodir.processing.service.dao.SwitcherRedisDAO


@Service
class SwitcherRedisDAOImpl(
    private val repository: SwitcherRepository
) : SwitcherRedisDAO {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun findByName(switcher: String): Mono<Switcher?> {
        log.debug("Find switcher by name: $switcher")

        return Mono.fromCallable {
            repository.findByName(switcher)

        }.subscribeOn(Schedulers.boundedElastic())
            .doOnNext {
                log.debug("Find switcher by name: $switcher")
            }
    }
}