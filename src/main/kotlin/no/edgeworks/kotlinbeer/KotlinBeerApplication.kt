package no.edgeworks.kotlinbeer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class KotlinBeerApplication {

    companion object {
        lateinit var applicationContext: ConfigurableApplicationContext

        @JvmStatic
        fun main(args: Array<String>) {
            applicationContext = runApplication<KotlinBeerApplication>(*args)
        }

        @JvmStatic
        fun stop() {
            SpringApplication.exit(applicationContext)
        }

    }
}



