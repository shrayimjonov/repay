package uz.nodir.processing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProcessingApplication

fun main(args: Array<String>) {
    runApplication<ProcessingApplication>(*args)
}
