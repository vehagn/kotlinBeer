package no.deltahouse.kotlinbeer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinBeerApplication

fun main(args: Array<String>) {
    runApplication<KotlinBeerApplication>(*args)
}
