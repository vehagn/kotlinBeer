package no.deltahouse.kotlinbeer.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.domain.User
import no.deltahouse.kotlinbeer.model.dto.UserDTO
import no.deltahouse.kotlinbeer.service.TransactionService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.ZonedDateTime
import java.util.*

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

    @Autowired
    lateinit var mapper: ObjectMapper

    private val testUser = UserDAO(
        1,
        10,
        "Test",
        "Testusen",
        ZonedDateTime.now().minusYears(20),
        "test",
        "BFY",
        true,
        null,
        100,
        200,
        Collections.emptyList()
    )

    @Test
    fun purchase() {
        every { mockTransactionService.purchase(any(), any()) } returns User(testUser)
        mockMvc.post("/user/123/buy?value=100")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(UserDTO(User(testUser)))) }
            }
    }
}