package no.edgeworks.kotlinbeer.user

import no.edgeworks.kotlinbeer.exceptions.CardIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.EmailIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.UserIsDeletedException
import no.edgeworks.kotlinbeer.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class UserService(
    @Autowired val userRepository: UserRepository
) {

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
            .filter { it.deletedDate == null }
            .map { User(it) }
    }

    fun getUserByCardId(cardId: Long): User {
        return User(getUserDAOByCardId(cardId))
    }

    fun createUser(user: User, createdBy: String): User {
        if (userRepository.findByCardId(user.cardId).isPresent) {
            throw CardIsAlreadyRegisteredException()
        }
        if (userRepository.findByEmail(user.email).isPresent) {
            throw EmailIsAlreadyRegisteredException()
        }

        return User(userRepository.save(UserDAO(user, createdBy)))
    }

    fun updateUserDetails(updatedUser: User, changedBy: String): User {
        val oldUserDAO = getUserDAOByCardId(updatedUser.cardId)
        val oldUser = User(oldUserDAO)

        // We shouldn't be able to update user with an email address registered to a different user
        if (oldUser.email != updatedUser.email && userRepository.findByEmail(updatedUser.email).isPresent) {
            throw EmailIsAlreadyRegisteredException()
        }

        var userProperties = oldUserDAO.userProperties.toMutableSet()

        // Check for changed user title
        if (oldUser.title != updatedUser.title) {
            userProperties = userProperties.filterNotTo(mutableSetOf()) { it.type == UserPropertyType.TITLE }
            if (updatedUser.title != null) {
                userProperties.add(UserPropertyDAO(UserPropertyType.TITLE, updatedUser.title, changedBy))
            }
        }

        // Check for changed credit rating
        if (oldUser.creditRating != updatedUser.creditRating) {
            userProperties = userProperties.filterNotTo(mutableSetOf()) { it.type == UserPropertyType.CREDIT }
            val creditRating = updatedUser.creditRating?.takeIf { it >= 0 } ?: 0
            userProperties.add(UserPropertyDAO(UserPropertyType.CREDIT, creditRating.toString(), changedBy))
        }

        if (oldUser.comments != updatedUser.comments) {
            // Remove comments only in old set
            val commentsToDelete = oldUser.comments.minus(updatedUser.comments)
            userProperties = userProperties.filterNotTo(mutableSetOf()) {
                it.type == UserPropertyType.COMMENT && commentsToDelete.contains(it.value)
            }
            // Add comments only in new set
            val commentsToAdd = updatedUser.comments.minus(oldUser.comments)
            commentsToAdd.forEach { userProperties.add(UserPropertyDAO(UserPropertyType.COMMENT, it, changedBy)) }
            // Ignore comments in both old and new set
        }

        return User(
            userRepository.save(
                oldUserDAO.copy(
                    firstName = updatedUser.firstName,
                    lastName = updatedUser.lastName,
                    birthday = updatedUser.birthday,
                    email = updatedUser.email,
                    userGroup = updatedUser.userGroup,
                    isMember = updatedUser.isMember,
                    userProperties = userProperties,
                    changedBy = changedBy
                )
            )
        )
    }

    fun changeUserCardId(email: String, newCardId: Long, changedBy: String): User {
        if (userRepository.findByCardId(newCardId).isPresent) {
            throw CardIsAlreadyRegisteredException()
        }

        val userDAO = userRepository.findByEmail(email)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        return User(userRepository.save(userDAO.get().copy(cardId = newCardId, changedBy = changedBy)))
    }

    fun deleteUser(cardId: Long): UserDAO {
        // Soft delete user
        val userDAO = getUserDAOByCardId(cardId)
        return userRepository.save(userDAO.copy(deletedDate = ZonedDateTime.now()))
    }

    fun getUserDAOByCardId(cardId: Long): UserDAO {
        val userDAO = userRepository.findByCardId(cardId)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        if (userDAO.get().deletedDate != null) {
            throw UserIsDeletedException()
        }
        return userDAO.get()
    }

}

