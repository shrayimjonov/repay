package uz.nodir.logprotocol

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(
    scanBasePackages =
    [
        "uz.nodir.logprotocol.*",
        "uz.repay.common.*",
        "uz.nodir.repayautoconfigure.*"
    ]
)
@EnableEncryptableProperties
class LogProtocolApplication

fun main(args: Array<String>) {
    runApplication<LogProtocolApplication>(*args)
}
