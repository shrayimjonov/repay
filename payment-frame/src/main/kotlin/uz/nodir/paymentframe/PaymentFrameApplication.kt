package uz.nodir.paymentframe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "uz.nodir.*",
        "uz.repay.*"
    ]
)
class PaymentFrameApplication

fun main(args: Array<String>) {
    runApplication<PaymentFrameApplication>(*args)
}
