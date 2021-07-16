package no.edgeworks.kotlinbeer.wallet

import io.mockk.every
import io.mockk.mockk
import no.edgeworks.kotlinbeer.user.UserDAO
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
import java.time.ZonedDateTime
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(WalletController::class)
internal class WalletControllerTest {
    private val testUserA = UserDAO(
        id = 1,
        cardId = 10,
        firstName = "Test A",
        lastName = "Testusen",
        email = "a@test.com",
        birthday = ZonedDateTime.now().minusYears(20),
        userGroup = "BFY",
        isMember = true,
        userProperties = Collections.emptySet(),
        createdBy = "Test"
    )

    private val walletA = WalletDAO(
        id = 1,
        user = testUserA,
        cashBalance = 0,
        totalSpent = 0,
    )

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun mockWalletService() = mockk<WalletService>()
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mockWalletService: WalletService

    @Test
    fun purchase() {
        every { mockWalletService.purchase(any(), any()) } returns UserWallet(testUserA, walletA)
        mockMvc.post("/users/123/wallet/buy?value=100")
            .andExpect {
                status { isOk() }
            }
    }
}