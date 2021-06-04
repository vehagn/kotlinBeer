package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.UserRepository
import no.deltahouse.kotlinbeer.model.domain.User
import no.deltahouse.kotlinbeer.model.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired val userRepository: UserRepository,
) {

    fun getAll(): List<User> {
        return userRepository.findAll()
            .map { dao -> User(dao) }
    }

    fun getByCardId(cardId: Long): User {
        val userDAO = userRepository.findByCardId(cardId)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        return User(userDAO.get())
    }

}
