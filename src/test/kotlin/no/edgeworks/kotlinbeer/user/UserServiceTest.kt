package no.edgeworks.kotlinbeer.user

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import no.edgeworks.kotlinbeer.exceptions.CardIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.EmailIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.UserIsDeletedException
import no.edgeworks.kotlinbeer.exceptions.UserNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class UserServiceTest {

    private val mockUserRepository = mockk<UserRepository>()

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

    private val testUserB = testUserA.copy(id = 2, cardId = 20, firstName = "Test B")
    private val testUserC =
        testUserA.copy(id = 3, cardId = 30, firstName = "Deleted user", deletedDate = ZonedDateTime.now())

    @InjectMockKs
    private val userService = UserService(mockUserRepository)

    @Test
    fun getAllUsers() {
        every { mockUserRepository.findAll() } returns listOf(testUserA, testUserB, testUserC)

        assertEquals(
            listOf(User(testUserA), User(testUserB)),
            userService.getAllUsers(),
            "We should only get non-deleted users."
        )
    }

    @Test
    fun getUserByCardId() {
        every { mockUserRepository.findByCardId(any()) } returns Optional.empty()
        every { mockUserRepository.findByCardId(testUserA.cardId) } returns Optional.of(testUserA)
        every { mockUserRepository.findByCardId(testUserB.cardId) } returns Optional.of(testUserB)
        every { mockUserRepository.findByCardId(testUserC.cardId) } returns Optional.of(testUserC)

        assertEquals(
            User(testUserA),
            userService.getUserByCardId(testUserA.cardId),
            "We should be able to retrieve a user by its card ID."
        )

        var notRegisteredCardId: Long
        do {
            notRegisteredCardId = Random.nextLong()
        } while (setOf(testUserA.cardId, testUserB.cardId, testUserC.cardId).contains(notRegisteredCardId))

        assertThrows<UserNotFoundException>("We should get an exception if no user has the given card ID.") {
            userService.getUserByCardId(
                notRegisteredCardId
            )
        }

        assertThrows<UserIsDeletedException>("We should get an exception if user has been soft deleted.") {
            userService.getUserByCardId(
                testUserC.cardId
            )
        }
    }

    @Test
    fun createUser() {
        every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUserA) andThen Optional.empty()
        every { mockUserRepository.findByEmail(any()) } returns Optional.of(testUserA) andThen Optional.empty()

        val newUser = User(testUserA)
        every { mockUserRepository.save(any()) } returns UserDAO(newUser, testUserA.createdBy)

        assertThrows<CardIsAlreadyRegisteredException>("We should not be able to register the same card ID twice") {
            userService.createUser(
                newUser,
                "Should fail"
            )
        }
        assertThrows<EmailIsAlreadyRegisteredException>("We should not be able to register the same email twice.") {
            userService.createUser(
                newUser,
                "Should fail"
            )
        }

        assertEquals(
            newUser,
            userService.createUser(newUser, testUserA.createdBy),
            "We should be able to register a new user if neither the card ID, not the email has already been registered."
        )
    }

    @Test
    fun updateUserDetails() {
        val updatedUser = User(testUserA).copy(
            firstName = "New name",
            title = "Title",
            email = "new@email.com",
            comments = setOf("Comment A", "Comment B"),
            isMember = !testUserA.isMember,
            creditRating = 1
        )

        every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUserA)
        every { mockUserRepository.findByEmail(any()) } returns Optional.of(testUserA) andThen Optional.empty()

        assertThrows<EmailIsAlreadyRegisteredException>("We shouldn't be able to change an email to one already registered") {
            userService.updateUserDetails(
                updatedUser,
                "Should fail"
            )
        }

        every { mockUserRepository.save(any()) } returns UserDAO(updatedUser, testUserA.createdBy)

        assertEquals(updatedUser, userService.updateUserDetails(updatedUser, "Ignored in domain object"))
    }

    @Test
    fun changeUserCardId() {
        val newCardId = testUserA.cardId + 1

        every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUserB) andThen Optional.empty()

        assertThrows<CardIsAlreadyRegisteredException>("We shouldn't be able to update the card ID to a card ID already registered.") {
            userService.changeUserCardId(
                testUserA.email,
                newCardId,
                "Test"
            )
        }

        every { mockUserRepository.findByEmail(any()) } returns Optional.empty() andThen Optional.of(testUserA)

        assertThrows<UserNotFoundException>("We can't update the card ID if we can't identify the user by email.") {
            userService.changeUserCardId(
                testUserA.email,
                newCardId,
                "Test"
            )
        }

        every { mockUserRepository.save(any()) } returns testUserA.copy(cardId = newCardId)

        assertEquals(
            User(testUserA).copy(cardId = newCardId),
            userService.changeUserCardId(testUserA.email, newCardId, "Test"), "We should be able to update the card ID."
        )
    }

    @Test
    fun deleteUser() {
        every { mockUserRepository.findByCardId(any()) } returns Optional.of(testUserA)
        every { mockUserRepository.save(any()) } returns testUserA.copy(deletedDate = ZonedDateTime.now())

        assertNotNull(userService.deleteUser(testUserA.cardId).deletedDate, "Deleting a user should set a non-null deleted date.")
    }
}