package no.edgeworks.kotlinbeer.wallet

import no.edgeworks.kotlinbeer.user.UserController
import no.edgeworks.kotlinbeer.user.UserRepository
import no.edgeworks.kotlinbeer.user.UserService
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback database after each test
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
internal class WalletIntegrationTests {

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var walletService: WalletService

    @Autowired
    private lateinit var walletController: WalletController

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userController: UserController

    @BeforeAll
    fun init(@Autowired dataSource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration", "classpath:db/testdata")
            .load()

        flyway.migrate()
    }

    @Nested
    @DisplayName("Transaction integration tests")
    inner class TransactionIntegrationTests {
        @Test
        fun `verify wallet test data`() {

        }
    }

}