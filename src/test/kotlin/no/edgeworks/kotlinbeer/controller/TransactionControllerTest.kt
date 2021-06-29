package no.edgeworks.kotlinbeer.controller

import io.mockk.every
import io.mockk.mockk
import no.edgeworks.kotlinbeer.service.TransactionService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(TransactionController::class)
internal class TransactionControllerTest {

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun mockTransactionService() = mockk<TransactionService>()
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mockTransactionService: TransactionService

    @Test
    fun purchase() {
        every { mockTransactionService.purchase(any(), any()) } returns Unit
        mockMvc.post("/users/123/buy?value=100")
            .andExpect {
                status { isOk() }
            }
    }
}