package no.edgeworks.kotlinbeer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class KotlinBeerApplicationTests {

    @Test
    fun contextLoads() {
        System.setProperty("spring.profiles.active", "test")
        System.setProperty("server.port", "2501")
        assertDoesNotThrow { KotlinBeerApplication.main(arrayOf("")) }
        assertDoesNotThrow { KotlinBeerApplication.stop() }
    }

}