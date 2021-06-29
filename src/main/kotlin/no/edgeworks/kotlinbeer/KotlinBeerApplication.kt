package no.edgeworks.kotlinbeer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class KotlinBeerApplication

lateinit var applicationContext: ConfigurableApplicationContext

fun main(args: Array<String>) {
    applicationContext = runApplication<KotlinBeerApplication>(*args)
}

fun stop() {
    SpringApplication.exit(applicationContext)
}


