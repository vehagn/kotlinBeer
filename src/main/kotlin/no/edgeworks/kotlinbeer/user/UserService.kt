package no.edgeworks.kotlinbeer.user

import no.edgeworks.kotlinbeer.exceptions.CardIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.EmailIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.exceptions.UserIsDeletedException
import no.edgeworks.kotlinbeer.exceptions.UserNotFoundException
import no.edgeworks.kotlinbeer.wallet.WalletRepository
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

        val userProperties = mutableSetOf<UserPropertyDAO>()

        if (user.title != null) {
            userProperties.add(UserPropertyDAO(UserPropertyType.TITLE, user.title, createdBy))
        }
        if (user.creditRating != null && user.creditRating.toByte() > 0) {
            userProperties.add(UserPropertyDAO(UserPropertyType.CREDIT, user.creditRating.toString(), createdBy))
        }
        user.comments.forEach { userProperties.add(UserPropertyDAO(UserPropertyType.COMMENT, it, createdBy)) }

        return User(userRepository.save(UserDAO(user, userProperties, createdBy)))
    }

    fun updateUserDetails(updatedUser: User, changedBy: String) {
        val oldUser = getUserByCardId(updatedUser.cardId)

        // Check for basic details
        if (oldUser.firstName != updatedUser.firstName ||
            oldUser.lastName != updatedUser.lastName ||
            oldUser.birthday != updatedUser.birthday ||
            oldUser.email != updatedUser.email ||
            oldUser.studprog != updatedUser.studprog ||
            oldUser.isMember != updatedUser.isMember
        ) {
            if (userRepository.findByEmail(updatedUser.email).isPresent) {
                throw EmailIsAlreadyRegisteredException()
            }
            val userDAO = getUserDAOByCardId(updatedUser.cardId)
            userRepository.save(
                userDAO.copy(
                    firstName = updatedUser.firstName,
                    lastName = updatedUser.lastName,
                    birthday = updatedUser.birthday,
                    email = updatedUser.email,
                    studprog = updatedUser.studprog,
                    isMember = updatedUser.isMember,
                    changedBy = changedBy
                )
            )
        }

        // Check for updated title
        if (oldUser.title != updatedUser.title) {
            if (updatedUser.title != null) {
                setUserTitle(oldUser.cardId, updatedUser.title, changedBy)
            } else {
                deleteUserTitle(oldUser.cardId)
            }
        }

        // Check for updated credit rating
        if (oldUser.creditRating != updatedUser.creditRating) {
            setUserCreditRating(oldUser.cardId, updatedUser.creditRating?.takeIf { it >= 0 } ?: 0, changedBy)
        }

        if (oldUser.comments != updatedUser.comments) {
            // Delete comments only in old set
            deleteUserComments(oldUser.cardId, oldUser.comments.minus(updatedUser.comments))
            // Add comments only in new set
            addUserComments(oldUser.cardId, updatedUser.comments.minus(oldUser.comments), changedBy)
            // Ignore comments in both old and new set
        }

    }

    fun changeUserCardId(email: String, newCardId: Long, changedBy: String) {
        if (userRepository.findByCardId(newCardId).isPresent) {
            throw CardIsAlreadyRegisteredException()
        }

        val userDAO = userRepository.findByEmail(email)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        userRepository.save(userDAO.get().copy(cardId = newCardId, changedBy = changedBy))
    }

    fun setUserTitle(cardId: Long, title: String, changedBy: String) {
        val userDAO = getUserDAOByCardId(cardId)
        val oldTitle = userDAO.userProperties.find { it.type == UserPropertyType.TITLE }?.value
        if (title != oldTitle) {
            val userProperties = userDAO.userProperties
                .filterNot { it.type == UserPropertyType.TITLE }.toMutableSet()
            userProperties.add(UserPropertyDAO(UserPropertyType.TITLE, title, createdBy = changedBy))
            userRepository.save(userDAO.copy(userProperties = userProperties))
        }
    }

    fun deleteUserTitle(cardId: Long) {
        val userDAO = getUserDAOByCardId(cardId)
        val userProperties = userDAO.userProperties
            .filterNot { it.type == UserPropertyType.TITLE }.toSet()
        userRepository.save(userDAO.copy(userProperties = userProperties))
    }

    fun setUserCreditRating(cardId: Long, rating: Byte, responsible: String) {
        val userDAO = getUserDAOByCardId(cardId)

        val userProperties = userDAO.userProperties
            .filterNot { it.type == UserPropertyType.CREDIT }.toMutableSet()
        userProperties.add(UserPropertyDAO(UserPropertyType.CREDIT, rating.toString(), createdBy = responsible))
        userRepository.save(userDAO.copy(userProperties = userProperties))
    }

    fun addUserComments(cardId: Long, comments: Set<String>, createdBy: String) {
        val userDAO = getUserDAOByCardId(cardId)

        val userProperties = userDAO.userProperties.toMutableSet()
        comments.forEach { userProperties.add(UserPropertyDAO(UserPropertyType.COMMENT, it, createdBy)) }
        userRepository.save(userDAO.copy(userProperties = userProperties))
    }

    fun deleteUserComments(cardId: Long, commentsToDelete: Set<String>) {
        val userDAO = getUserDAOByCardId(cardId)

        val userProperties = userDAO.userProperties
            .filterNot { it.type == UserPropertyType.COMMENT && commentsToDelete.contains(it.value) }.toSet()
        userRepository.save(userDAO.copy(userProperties = userProperties))
    }

    fun deleteUser(cardId: Long) {
        // Soft delete user
        val userDAO = getUserDAOByCardId(cardId)
        userRepository.save(userDAO.copy(deletedDate = ZonedDateTime.now()))
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

