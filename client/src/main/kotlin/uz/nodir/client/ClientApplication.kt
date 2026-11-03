package uz.nodir.client

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(
    scanBasePackages = [
        "uz.nodir.*",
        "uz.repay.*"
    ]
)
@EnableFeignClients
@EnableEncryptableProperties
class ClientApplication

fun main(args: Array<String>) {
    runApplication<ClientApplication>(*args)
}
