package uz.nodir.processing.redis.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.util.UUID


@RedisHash(value = "switcher")
data class Switcher (
    @Id
    @Indexed
    val id: UUID,

    @Indexed
    val name: String,

    val isEnabled: Boolean
)