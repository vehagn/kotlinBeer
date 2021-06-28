package no.edgeworks.kotlinbeer.service

import no.edgeworks.kotlinbeer.database.UserPropertyRepository
import no.edgeworks.kotlinbeer.database.UserRepository
import no.edgeworks.kotlinbeer.model.constants.UserPropertyType
import no.edgeworks.kotlinbeer.model.dao.UserDAO
import no.edgeworks.kotlinbeer.model.dao.UserPropertyDAO
import no.edgeworks.kotlinbeer.model.domain.User
import no.edgeworks.kotlinbeer.model.exceptions.CardIsAlreadyRegisteredException
import no.edgeworks.kotlinbeer.model.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired val userRepository: UserRepository,
    @Autowired val userPropertyRepository: UserPropertyRepository
) {

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
            .map { User(it) }
    }

    fun getUserByCardId(cardId: Long): User {
        return User(getUserDAOByCardId(cardId))
    }

    fun createUser(user: User, createdBy: String): User {
        val userDAO = userRepository.findByCardId(user.cardId)
        if (userDAO.isPresent) {
            throw CardIsAlreadyRegisteredException()
        }
        val userProperties = mutableSetOf<UserPropertyDAO>()

        if (user.title != null) {
            userProperties.add(UserPropertyDAO(UserPropertyType.TITLE, user.title, createdBy))
        }
        if (user.creditRating != null && user.creditRating.toByte() > 0) {
            userProperties.add(UserPropertyDAO(UserPropertyType.COMMENT, user.creditRating.toString(), createdBy))
        }
        user.comments.forEach { userProperties.add(UserPropertyDAO(UserPropertyType.COMMENT, it, createdBy)) }

        return User(userRepository.save(UserDAO(user, userProperties, createdBy)))
    }

    fun updateUser(updatedUser: User, changedBy: String) {
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

    fun changeUserCardId(email: String, newCardId: Long, changedBy: String) {
        val userDAO = userRepository.findByEmail(email)

        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        userRepository.save(userDAO.get().copy(cardId = newCardId, changedBy = changedBy))
    }

    fun setUserTitle(cardId: Long, title: String, responsible: String) {
        val userDAO = getUserDAOByCardId(cardId)

        val userTitle = userDAO.userProperties.find { it.type == UserPropertyType.TITLE }
        if (userTitle == null) {
            val userProperties = userDAO.userProperties.toMutableSet()
            userProperties.add(UserPropertyDAO(UserPropertyType.TITLE, title, createdBy = responsible))
            userRepository.save(userDAO.copy(userProperties = userProperties))
        } else {
            userPropertyRepository.save(userTitle.copy(value = title, changedBy = responsible))
        }
    }

    fun setUserCreditRating(cardId: Long, rating: Byte, responsible: String) {
        val userDAO = getUserDAOByCardId(cardId)

        val creditRating = userDAO.userProperties.find { it.type == UserPropertyType.TAB }
        if (creditRating == null) {
            val userProperties = userDAO.userProperties.toMutableSet()
            userProperties.add(UserPropertyDAO(UserPropertyType.TAB, rating.toString(), createdBy = responsible))
            userRepository.save(userDAO.copy(userProperties = userProperties))
        } else {
            userPropertyRepository.save(creditRating.copy(value = rating.toString(), changedBy = responsible))
        }
    }

    fun addUserComment(cardId: Long, comment: String, createdBy: String) {
        val userDAO = getUserDAOByCardId(cardId)

        val userProperties = userDAO.userProperties.toMutableSet()
        userProperties.add(UserPropertyDAO(UserPropertyType.TITLE, comment, createdBy))
        userRepository.save(userDAO.copy(userProperties = userProperties))
    }

    fun deleteUserComment(cardId: Long, commentToDelete: String) {
        val userDAO = getUserDAOByCardId(cardId)

        val userComment = userDAO.userProperties.filter { it.type == UserPropertyType.TITLE }
            .find { it.value.equals(commentToDelete, ignoreCase = true) }
        if (userComment != null) {
            userPropertyRepository.delete(userComment)
        }
    }

    fun deleteUser(cardId: Long) {
        val userDAO = userRepository.findByCardId(cardId)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        userRepository.delete(userDAO.get())
    }

    fun getUserDAOByCardId(cardId: Long): UserDAO {
        val userDAO = userRepository.findByCardId(cardId)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        return userDAO.get()
    }

}
