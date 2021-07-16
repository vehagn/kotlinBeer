package no.edgeworks.kotlinbeer.user

import no.edgeworks.kotlinbeer.exceptions.CardIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.EmailIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.UserIsDeletedException
import no.edgeworks.kotlinbeer.exceptions.UserNotFoundException
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import javax.sql.DataSource
import kotlin.random.Random
import kotlin.test.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback database after each test
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
internal class UserIntegrationTests {

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
    @DisplayName("User integration tests")
    inner class UserIntegrationTests {
        @Test
        fun `verify user test data`() {
            assertEquals(3, userRepository.findAll().size, "We should have three test users.")

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
        fun `fetch user test`() {
            val userDAO = userRepository.findById(1).get()
            val userDomain = User(userDAO)
            val userDTO = UserDTO(userDomain)

            assertEquals(
                userDAO,
                userRepository.findByCardId(userDAO.cardId).get(),
                "User found by ID and CardID should be the same."
            )
            assertEquals(
                userDomain,
                userService.getUserByCardId(userDAO.cardId),
                "User found by the service layer should be the same as the DAO user converted to a domain user."
            )
            assertEquals(
                userDTO,
                userController.getUserByCardId(userDAO.cardId),
                "User found by the controller layer should be the same as the domain user converted a DTO user."
            )
        }

        @Test
        fun `create user test`() {
            val usedCardIds = userRepository.findAll().mapTo(HashSet()) { it.cardId }

            val userDTO = UserDTO(
                cardId = -1,
                firstName = "FirstName",
                lastName = "LastName",
                email = "test@test.no",
                birthday = ZonedDateTime.now(),
                title = "title",
                comments = setOf("comment"),
                userGroup = "Test",
                isMember = true,
                creditRating = 1
            )

            assertThrows<CardIsAlreadyRegisteredException>("We should not be able to register a new user with an already existing cardId.") {
                userController.createUser(userDTO.copy(cardId = usedCardIds.random()))
            }

            val uniqueCardId = newCardId()
            assertThrows<UserNotFoundException>("We should not be able to find the user before it has been created.") {
                userController.getUserByCardId(
                    uniqueCardId
                )
            }

            val newUser = userDTO.copy(cardId = uniqueCardId)

            assertThrows<EmailIsAlreadyRegisteredException>("Supplied e-mail must be unique.") {
                userController.createUser(
                    newUser.copy(email = userRepository.findAll().random().email)
                )
            }

            assertDoesNotThrow("We should be able to create a new user without trouble.") {
                userController.createUser(newUser)
            }

            val createdUser = assertDoesNotThrow("We should be able to find the newly created user.") {
                userController.getUserByCardId(
                    uniqueCardId
                )
            }
            assertEquals(createdUser, newUser, "Created user should equal the one supplied.")
        }

        @Test
        fun `update user details test`() {
            val oldUser = userController.getUserByCardId(userRepository.findById(3).get().cardId)

            assertEquals(
                oldUser,
                userController.updateUserDetails(oldUser),
                "Updating the user details with the same details should not alter the user"
            )

            var updatedUser = oldUser.copy(
                firstName = "Updated first name",
                lastName = "Updated last name",
                birthday = ZonedDateTime.now(),
                email = "updated@e.mail",
                userGroup = "Updated",
                isMember = !oldUser.isMember
            )

            assertThrows<EmailIsAlreadyRegisteredException>("New e-mail must not already be registered.") {
                userController.updateUserDetails(updatedUser.copy(email = userRepository.findById(1).get().email))
            }

            // Test for basic details
            assertEquals(
                updatedUser,
                userController.updateUserDetails(updatedUser),
                "Updated user and supplied user should be equal when only updating basic details."
            )

            // Test for title
            assertNotNull(updatedUser.title, "Test user should have a title.")
            assertEquals(
                updatedUser.copy(title = "New title"),
                userController.updateUserDetails(updatedUser.copy(title = "New title")),
                "We should be able to change the user title."
            )

            assertEquals(
                updatedUser.copy(title = null),
                userController.updateUserDetails(updatedUser.copy(title = null)),
                "We should be able to remove the title"
            )

            // Test for credit rating
            assertNull(updatedUser.creditRating, "Credit rating should be null to begin with.")
            assertNull(
                userController.updateUserDetails(updatedUser.copy(creditRating = null)).creditRating,
                "Credit rating should remain 'null' if it was null to begin with."
            )

            assertEquals(
                5,
                userController.updateUserDetails(updatedUser.copy(creditRating = 5)).creditRating,
                "We should be able to set the credit rating."
            )
            assertEquals(
                0,
                userController.updateUserDetails(updatedUser.copy(creditRating = null)).creditRating,
                "Removing the credit rating should set it to 0 and not 'null'."
            )
            assertEquals(
                0,
                userController.updateUserDetails(updatedUser.copy(creditRating = -5)).creditRating,
                "We can't have a negative credit rating."
            )
            updatedUser = updatedUser.copy(creditRating = 0)

            // Test for comments
            assertEquals(setOf(), updatedUser.comments, "Test user should have no comments to begin with.")
            val commentsAB = setOf("Comment A", "Comment B")
            val commentsABC = setOf("Comment A", "Comment B", "Comment C")
            assertEquals(
                updatedUser.copy(comments = commentsABC),
                userController.updateUserDetails(updatedUser.copy(comments = commentsABC)),
                "We should be able to add comments to a user."
            )
            assertEquals(
                updatedUser.copy(comments = commentsAB),
                userController.updateUserDetails(updatedUser.copy(comments = commentsAB)),
                "We should be able to remove one comment."
            )
            assertEquals(
                setOf(),
                userController.updateUserDetails(updatedUser.copy(comments = setOf())).comments,
                "We should be able to remove all comments."
            )
        }

        @Test
        fun `change user card ID test`() {
            val user = userRepository.findById(2).get()

            assertThrows<CardIsAlreadyRegisteredException>("Card ID must be unique.") {
                userController.changeUserCardId(
                    "new@card.id",
                    user.cardId
                )
            }

            val unknownEmailAddress = "unknown@email.com"
            assertFalse("Assert that email address does not exist.") {
                userRepository.findAll().mapTo(HashSet()) { it.email }.contains(unknownEmailAddress)
            }

            val newCardId = newCardId()
            assertThrows<UserNotFoundException>("User is not found when searching for an unknown email address.") {
                userController.changeUserCardId(
                    unknownEmailAddress,
                    newCardId
                )
            }

            assertEquals(
                UserDTO(User(user.copy(cardId = newCardId))),
                userController.changeUserCardId(user.email, newCardId),
                "We should be able to update the user card id based on a correct email address."
            )
        }

        @Test
        fun `delete user test`() {
            assertThrows<UserNotFoundException>("Can't delete a non-existing user.") {
                userController.deleteUser(
                    newCardId()
                )
            }

            val userToDelete =
                assertDoesNotThrow("User to delete should already exists.") {
                    userController.getUserByCardId(
                        userRepository.getById(
                            1
                        ).cardId
                    )
                }

            assertDoesNotThrow("Should be able to delete users.") { userController.deleteUser(userToDelete.cardId) }

            assertThrows<UserIsDeletedException>("Should not be able to find deleted user.") {
                userController.getUserByCardId(
                    userToDelete.cardId
                )
            }
        }

        private fun newCardId(): Long {
            // Create a random cardId not already registered
            val usedCardIds = userRepository.findAll().mapTo(HashSet()) { it.cardId }
            var uniqueCardId: Long
            do {
                uniqueCardId = Random.nextLong()
            } while (usedCardIds.contains(uniqueCardId))
            return uniqueCardId
        }

    }

}