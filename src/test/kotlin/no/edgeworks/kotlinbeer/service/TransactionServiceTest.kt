package no.edgeworks.kotlinbeer.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import no.edgeworks.kotlinbeer.database.TransactionRepository
import no.edgeworks.kotlinbeer.database.WalletRepository
import no.edgeworks.kotlinbeer.model.constants.UserPropertyType
import no.edgeworks.kotlinbeer.model.dao.TransactionDAO
import no.edgeworks.kotlinbeer.model.dao.UserDAO
import no.edgeworks.kotlinbeer.model.dao.UserPropertyDAO
import no.edgeworks.kotlinbeer.model.dao.WalletDAO
import no.edgeworks.kotlinbeer.model.exceptions.InvalidTransactionException
import no.edgeworks.kotlinbeer.model.exceptions.UserNotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.*
import kotlin.test.Test

internal class TransactionServiceTest {

    private val mockUserService = mockk<UserService>()
    private val mockUserWalletRepository = mockk<WalletRepository>()
    private val mockTransactionRepository = mockk<TransactionRepository>()

    private val maxDepositValue = 1000
    private val maxPurchaseValue = 500
    private val creditMultiplier = 100

    private val testUser = UserDAO(
        id = 1,
        cardId = 10,
        firstName = "Test",
        lastName = "Testusen",
        email = "test",
        birthday = ZonedDateTime.now().minusYears(20),
        studprog = "BFY",
        isMember = true,
        userProperties = Collections.emptySet(),
        createdBy = "Test"
    )

    private val testUserWallet = WalletDAO(
        id = 1,
        user = testUser,
        cashBalance = 100,
        totalSpent = 0
    )

    private val testUserWithCredit = UserDAO(
        id = 2,
        cardId = 10,
        firstName = "Test",
        lastName = "Testusen",
        email = "test",
        birthday = ZonedDateTime.now().minusYears(20),
        studprog = "BFY",
        isMember = true,
        userProperties = setOf(UserPropertyDAO(UserPropertyType.CREDIT, "2", "Test")),
        createdBy = "Test"
    )

    private val testUserWithCreditWallet = WalletDAO(
        id = 2,
        user = testUser,
        cashBalance = 100,
        totalSpent = 0
    )

    private val testUserWithBigCashBalance = UserDAO(
        id = 3,
        cardId = 10,
        firstName = "Test",
        lastName = "Testusen",
        email = "test",
        birthday = ZonedDateTime.now().minusYears(20),
        studprog = "BFY",
        isMember = true,
        userProperties = setOf(UserPropertyDAO(UserPropertyType.CREDIT, "2", "Test")),
        createdBy = "Test"
    )

    private val testUserWithBigCashBalanceWallet = WalletDAO(
        id = 2,
        user = testUser,
        cashBalance = Int.MAX_VALUE,
        totalSpent = Int.MAX_VALUE,
    )

    @InjectMockKs
    private val transactionService: TransactionService = TransactionService(
        mockUserService,
        mockUserWalletRepository,
        mockTransactionRepository,
        maxDepositValue,
        maxPurchaseValue,
        creditMultiplier
    )

    @Nested
    @DisplayName("TransactionService::purchase() test")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Purchase {
        @BeforeEach
        fun init() {
            every { mockUserService.getUserDAOByCardId(1) } returns testUser
            every { mockUserWalletRepository.findByUserId(testUser.id) } returns Optional.of(testUserWallet)

            every { mockUserService.getUserDAOByCardId(2) } returns testUserWithCredit
            every { mockUserWalletRepository.findByUserId(testUserWithCredit.id) } returns Optional.of(
                testUserWithCreditWallet
            )

            every { mockTransactionRepository.save(any()) } returns TransactionDAO(testUserWallet, 100)
            every { mockUserWalletRepository.save(any()) } returns testUserWallet
        }

        @Test
        fun `assumptions for tests`() {
            assertEquals(maxPurchaseValue, 500)
            assertEquals(creditMultiplier, 100)

            assertTrue { testUserWallet.cashBalance < maxPurchaseValue }
            assertEquals(testUserWallet.cashBalance, 100)
            assertNull(testUser.userProperties.find { it.type == UserPropertyType.CREDIT })

            assertTrue { testUserWithCreditWallet.cashBalance < maxPurchaseValue }
            assertEquals(testUserWithCreditWallet.cashBalance, 100)
            assertNotNull(testUserWithCredit.userProperties.find { it.type == UserPropertyType.CREDIT })
            assertEquals(testUserWithCredit.userProperties.find { it.type == UserPropertyType.CREDIT }?.value?.toByte(), 2)

            assertEquals(testUserWithBigCashBalanceWallet.cashBalance, Int.MAX_VALUE)
        }

        @Test
        fun `user not found during purchase`() {
            every { mockUserService.getUserDAOByCardId(any()) } throws UserNotFoundException()
            assertThrows<UserNotFoundException> { transactionService.purchase(1, 10) }
        }

        @Test
        fun `exceeding purchase value`() {
            val exception = assertThrows<InvalidTransactionException> {
                transactionService.purchase(
                    1,
                    (maxPurchaseValue + 1).toShort()
                )
            }
            assertTrue { exception.message?.contains("value too high") ?: false }
        }

        @Test
        fun `negative purchase value`() {
            val exception = assertThrows<InvalidTransactionException> { transactionService.purchase(1, 0) }
            assertTrue { exception.message?.contains("must be positive") ?: false }
        }

        @Test
        fun `not enough cash balance`() {
            val exception = assertThrows<InvalidTransactionException> {
                transactionService.purchase(
                    1,
                    (testUserWallet.cashBalance + 1).toShort()
                )
            }
            assertTrue { exception.message?.contains("Not enough funds to complete purchase") ?: false }
            assertFalse { exception.message?.contains("with a tab of") ?: true }
        }

        @Test
        fun `not enough cash balance with tab`() {
            val exception = assertThrows<InvalidTransactionException> {
                transactionService.purchase(
                    2,
                    (testUserWithCreditWallet.cashBalance + (2.times(creditMultiplier) + 1)).toShort()
                )
            }
            assertTrue { exception.message?.contains("Not enough funds to complete purchase") ?: false }
            assertTrue { exception.message?.contains("with a tab of") ?: true }
        }

        @TestFactory
        fun `purchase transactions with no tab`() =
            listOf(
                10 to null,
                20 to null,
                100 to null,
                testUserWallet.cashBalance to null,
                testUserWallet.cashBalance + 1 to "Not enough funds",
                maxPurchaseValue to "Not enough funds",
                maxPurchaseValue + 1 to "value too high",
                0 to "must be positive",
                -1 to "must be positive",
            ).map { (value, errorMessage) ->
                dynamicTest(
                    "Purchase of $value with a balance of ${testUserWallet.cashBalance} and no tab should "
                            + if (errorMessage == null) "be OK." else "give an error containing '$errorMessage'."
                ) {
                    if (errorMessage == null) {
                        assertDoesNotThrow { transactionService.purchase(1, value.toShort()) }
                    } else {
                        val exception = assertThrows<InvalidTransactionException> {
                            transactionService.purchase(
                                1,
                                value.toShort()
                            )
                        }
                        assertTrue { exception.message?.contains(errorMessage) ?: false }
                    }
                }
            }

        @TestFactory
        fun `purchase transactions with a tab`() =
            listOf(
                10 to null,
                20 to null,
                100 to null,
                testUserWithCreditWallet.cashBalance to null,
                testUserWithCreditWallet.cashBalance + 1 to null,
                testUserWithCreditWallet.cashBalance + 2.times(creditMultiplier) to null,
                testUserWithCreditWallet.cashBalance + 2.times(creditMultiplier) + 1 to "Not enough funds",
                maxPurchaseValue to "Not enough funds",
                maxPurchaseValue + 1 to "value too high",
                0 to "must be positive",
                -1 to "must be positive",
            ).map { (value, errorMessage) ->
                dynamicTest(
                    "Purchase of $value with a balance of ${testUserWithCreditWallet.cashBalance} and a tab of ${
                        2.times(creditMultiplier)
                    } should " + if (errorMessage == null) "be OK." else "throw an exception containing '$errorMessage'."
                ) {
                    if (errorMessage == null) {
                        assertDoesNotThrow { transactionService.purchase(2, value.toShort()) }
                    } else {
                        val exception = assertThrows<InvalidTransactionException> {
                            transactionService.purchase(
                                2,
                                value.toShort()
                            )
                        }
                        assertTrue { exception.message?.contains(errorMessage) ?: false }
                    }

                }
            }

    }


    @Nested
    @DisplayName("TransactionService::deposit() tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Deposit {

        @BeforeEach
        fun init() {
            every { mockUserService.getUserDAOByCardId(1) } returns testUser
            every { mockUserWalletRepository.findByUserId(testUser.id) } returns Optional.of(testUserWallet)

            every { mockUserService.getUserDAOByCardId(3) } returns testUserWithCredit
            every { mockUserWalletRepository.findByUserId(testUserWithCredit.id) } returns Optional.of(
                testUserWithBigCashBalanceWallet
            )

            every { mockTransactionRepository.save(any()) } returns TransactionDAO(testUserWallet, 100)
            every { mockUserWalletRepository.save(any()) } returns testUserWallet
        }

        @Test
        fun `assumptions for tests`() {
            assertEquals(maxDepositValue, 1000)
            assertEquals(creditMultiplier, 100)

            assertEquals(testUserWallet.cashBalance, 100)
            assertEquals(testUserWithBigCashBalanceWallet.cashBalance, Int.MAX_VALUE)
        }

        @Test
        fun `user not found during deposit`() {
            every { mockUserService.getUserDAOByCardId(any()) } throws UserNotFoundException()
            assertThrows<UserNotFoundException> { transactionService.deposit(1, 10) }
        }

        @TestFactory
        fun `deposit transactions`() = listOf(
            10 to null,
            20 to null,
            maxDepositValue to null,
            maxDepositValue + 1 to "value too high",
            0 to "must be positive",
            -1 to "must be positive",
        ).map { (value, errorMessage) ->
            dynamicTest("Deposit of ${value} should " + if (errorMessage == null) "be OK." else "throw an exception containing $errorMessage.") {
                if (errorMessage == null) {
                    assertDoesNotThrow { transactionService.deposit(1, value.toShort()) }
                } else {
                    val exception =
                        assertThrows<InvalidTransactionException> { transactionService.deposit(1, value.toShort()) }
                    assertTrue { exception.message?.contains(errorMessage) ?: false }
                }
            }
        }

        @Test
        fun `cash balance integer overflow prevention`() {
            var exception = assertThrows<InvalidTransactionException> { transactionService.deposit(3, 1) }
            assertTrue { exception.message?.contains("integer overflow") ?: false }

            exception =
                assertThrows { transactionService.deposit(3, maxDepositValue.toShort()) }
            assertTrue { exception.message?.contains("integer overflow") ?: false }
        }
    }
}