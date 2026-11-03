package uz.nodir.court

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(
    scanBasePackages = [
        "uz.nodir.court.*",
        "uz.repay.*",
        "uz.nodir.repayautoconfigure.*"
    ]
)
@EnableFeignClients
class CourtApplication

fun main(args: Array<String>) {
    runApplication<CourtApplication>(*args)
}
