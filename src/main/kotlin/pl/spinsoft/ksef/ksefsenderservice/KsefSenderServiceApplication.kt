package pl.spinsoft.ksef.ksefsenderservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableScheduling
class KsefSenderServiceApplication

fun main(args: Array<String>) {
    runApplication<KsefSenderServiceApplication>(*args)
}

@RestController
class HelloController {
    @GetMapping("/")
    fun hello() = "Hello, World from Kotlin + Spring Boot!"
}
