package uz.nodir.processing.config

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration


@Configuration
@EnableEncryptableProperties
class EncryptConfig {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun init() {
        log.info("Enable encryptable properties")
    }

}