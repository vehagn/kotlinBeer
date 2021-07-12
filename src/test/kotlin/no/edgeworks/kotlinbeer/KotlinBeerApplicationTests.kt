package no.edgeworks.kotlinbeer

import no.edgeworks.kotlinbeer.controller.TransactionController
import no.edgeworks.kotlinbeer.controller.UserController
import no.edgeworks.kotlinbeer.database.UserRepository
import no.edgeworks.kotlinbeer.model.dto.UserDTO
import no.edgeworks.kotlinbeer.model.exceptions.UserNotFoundException
import no.edgeworks.kotlinbeer.service.LegacyMigrationService
import no.edgeworks.kotlinbeer.service.TransactionService
import no.edgeworks.kotlinbeer.service.UserService
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback after each test
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KotlinBeerApplicationTests {

    @Test
    fun contextLoads() {
        assertEquals(2, 1 + 1)
    }

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var transactionController: TransactionController

    @Autowired
    private lateinit var transactionService: TransactionService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var legacyMigrationService: LegacyMigrationService

    @BeforeAll
    fun init(@Autowired dataSource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration", "classpath:db/testdata")
            .load()

        flyway.migrate()
    }

    @Test
    fun `verify test user data`() {
        val user1 = userRepository.findById(1)
        assertTrue { user1.isPresent }
        assertEquals("Kari", user1.get().firstName)
        assertEquals("Nordmann", user1.get().lastName)

        val user2 = userRepository.findById(2)
        assertTrue { user2.isPresent }
        assertEquals("Ola", user2.get().firstName)
        assertEquals("Nordmann", user2.get().lastName)

        val user3 = userRepository.findById(3)
        assertTrue { user2.isPresent }
        assertEquals("Erich Christian", user3.get().firstName)
        assertEquals("Dahl", user3.get().lastName)

        val user4 = userRepository.findById(4)
        assertFalse { user4.isPresent }
    }

    @Test
    fun `create user`() {
        // Test user
        assertDoesNotThrow { userController.getUserByCardId(10L) }

        val cardId = 100L
        assertThrows<UserNotFoundException> { userController.getUserByCardId(cardId) }
        val userDTO = UserDTO(
            cardId = 100L,
            firstName = "FirstName",
            lastName = "LastName",
            email = "test@test.no",
            birthday = ZonedDateTime.now(),
            title = "title",
            comments = listOf("comment"),
            studprog = "BFY",
            isMember = true,
            creditRating = 1
        )
        userController.createUser(userDTO)

        assertDoesNotThrow { userController.getUserByCardId(cardId) }
    }
}