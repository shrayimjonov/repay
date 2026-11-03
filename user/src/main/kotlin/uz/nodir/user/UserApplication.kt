package uz.nodir.user

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "uz.repay.*",
        "uz.nodir.*"
    ]
)
@EnableEncryptableProperties
class UserApplication

fun main(args: Array<String>) {

    runApplication<UserApplication>(*args)
}
