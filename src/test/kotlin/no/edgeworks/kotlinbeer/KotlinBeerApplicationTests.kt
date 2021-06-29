package no.edgeworks.kotlinbeer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class KotlinBeerApplicationTests {

    @Test
    fun contextLoads() {
        assertEquals(2, 1 + 1)
    }
}