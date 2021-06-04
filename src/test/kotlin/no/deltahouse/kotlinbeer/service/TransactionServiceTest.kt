package no.deltahouse.kotlinbeer.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import no.deltahouse.kotlinbeer.database.TransactionRepository
import no.deltahouse.kotlinbeer.database.UserRepository
import no.deltahouse.kotlinbeer.model.dao.TransactionDAO
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.exceptions.InvalidTransactionException
import no.deltahouse.kotlinbeer.model.exceptions.UserNotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.*
import kotlin.test.Test

internal class TransactionServiceTest {

    private val mockUserRepository = mockk<UserRepository>()
    private val mockTransactionRepository = mockk<TransactionRepository>()

    private val maxDepositValue = 1000
    private val maxPurchaseValue = 500
    private val creditMultiplier = 100

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

    private val testUserWithCredit = UserDAO(
        2,
        10,
        "Test",
        "Testusen",
        ZonedDateTime.now().minusYears(20),
        "test",
        "BFY",
        true,
        2,
        100,
        200,
        Collections.emptyList()
    )

    private val testUserWithBigCashBalance = UserDAO(
        3,
        10,
        "Test",
        "Testusen",
        ZonedDateTime.now().minusYears(20),
        "test",
        "BFY",
        true,
        null,
        Int.MAX_VALUE,
        200,
        Collections.emptyList()
    )

    @InjectMockKs
    private val transactionService: TransactionService = TransactionService(
        mockUserRepository,
        mockTransactionRepository,
        maxDepositValue,
        maxPurchaseValue,
        creditMultiplier
    )

    @Nested
    @DisplayName("TransactionService::purchase() test")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Purchase {
        @Test
        fun `assumptions for tests`() {
            assertEquals(maxPurchaseValue, 500)
            assertEquals(creditMultiplier, 100)

            assertTrue { testUser.cashBalance < maxPurchaseValue }
            assertEquals(testUser.cashBalance, 100)
            assertNull(testUser.creditRating)

            assertTrue { testUserWithCredit.cashBalance < maxPurchaseValue }
            assertEquals(testUserWithCredit.cashBalance, 100)
            assertNotNull(testUserWithCredit.creditRating)
            assertEquals(testUserWithCredit.creditRating, 2)
        }

        @Test
        fun `user not found during purchase`() {
            every { mockUserRepository.findByCardId(any()) } returns Optional.empty()
            assertThrows<UserNotFoundException> { transactionService.purchase(1, 10) }
        }

        @Test
        fun `exceeding purchase value`() {
            every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUser)
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
            every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUser)
            val exception = assertThrows<InvalidTransactionException> { transactionService.purchase(1, 0) }
            assertTrue { exception.message?.contains("must be positive") ?: false }
        }

        @Test
        fun `not enough cash balance`() {
            every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUser)
            val exception = assertThrows<InvalidTransactionException> {
                transactionService.purchase(
                    1,
                    (testUser.cashBalance + 1).toShort()
                )
            }
            assertTrue { exception.message?.contains("Not enough funds to complete purchase") ?: false }
            assertFalse { exception.message?.contains("with a tab of") ?: true }
        }

        @Test
        fun `not enough cash balance with tab`() {
            every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUserWithCredit)
            val exception = assertThrows<InvalidTransactionException> {
                transactionService.purchase(
                    1,
                    (testUserWithCredit.cashBalance + (testUserWithCredit.creditRating?.times(creditMultiplier)
                        ?: 0) + 1).toShort()
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
                testUser.cashBalance to null,
                testUser.cashBalance + 1 to "Not enough funds",
                maxPurchaseValue to "Not enough funds",
                maxPurchaseValue + 1 to "value too high",
                0 to "must be positive",
                -1 to "must be positive",
            ).map { (value, errorMessage) ->
                dynamicTest(
                    "Purchase of $value with a balance of ${testUser.cashBalance} and no tab should "
                            + if (errorMessage == null) "be OK." else "give an error containing '$errorMessage'."
                ) {
                    every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUser)
                    every { mockTransactionRepository.save(any()) } returns TransactionDAO(testUser, 100)
                    every { mockUserRepository.save(any()) } returns testUser
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
                testUserWithCredit.cashBalance to null,
                testUserWithCredit.cashBalance + 1 to null,
                testUserWithCredit.cashBalance + (testUserWithCredit.creditRating?.times(creditMultiplier)
                    ?: 0) to null,
                testUserWithCredit.cashBalance + (testUserWithCredit.creditRating?.times(creditMultiplier)
                    ?: 0) + 1 to "Not enough funds",
                maxPurchaseValue to "Not enough funds",
                maxPurchaseValue + 1 to "value too high",
                0 to "must be positive",
                -1 to "must be positive",
            ).map { (value, errorMessage) ->
                dynamicTest(
                    "Purchase of $value with a balance of ${testUserWithCredit.cashBalance} and a tab of ${
                        testUserWithCredit.creditRating?.times(
                            creditMultiplier
                        )
                    } should " + if (errorMessage == null) "be OK." else "give an error containing '$errorMessage'."
                ) {
                    every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUserWithCredit)
                    every { mockTransactionRepository.save(any()) } returns TransactionDAO(testUserWithCredit, 100)
                    every { mockUserRepository.save(any()) } returns testUserWithCredit
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

    }


    @Nested
    @DisplayName("TransactionService::deposit() tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Deposit {
        @Test
        fun `assumptions for tests`() {
            assertEquals(maxDepositValue, 1000)
            assertEquals(creditMultiplier, 100)

            assertEquals(testUser.cashBalance, 100)
            assertEquals(testUserWithBigCashBalance.cashBalance, Int.MAX_VALUE)
        }

        @Test
        fun `user not found during deposit`() {
            every { mockUserRepository.findByCardId(any()) } returns Optional.empty()
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
            dynamicTest("LOL") {
                every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUser)
                every { mockTransactionRepository.save(any()) } returns TransactionDAO(testUser, 100)
                every { mockUserRepository.save(any()) } returns testUser
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
            every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUserWithBigCashBalance)
            every { mockTransactionRepository.save(any()) } returns TransactionDAO(testUserWithBigCashBalance, 100)
            every { mockUserRepository.save(any()) } returns testUserWithBigCashBalance

            var exception = assertThrows<InvalidTransactionException> { transactionService.deposit(3, 1) }
            assertTrue { exception.message?.contains("integer overflow") ?: false }

            exception =
                assertThrows<InvalidTransactionException> { transactionService.deposit(3, maxDepositValue.toShort()) }
            assertTrue { exception.message?.contains("integer overflow") ?: false }
        }
    }
}