package no.edgeworks.kotlinbeer.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserDAO, Long> {
    fun findByCardId(cardId: Long): Optional<UserDAO>

    fun findByEmail(username: String): Optional<UserDAO>

}