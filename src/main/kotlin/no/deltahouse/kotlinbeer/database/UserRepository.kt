package no.deltahouse.kotlinbeer.database

import no.deltahouse.kotlinbeer.model.dao.UserDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserDAO, Long> {
    fun findByCardId(cardId: Long): Optional<UserDAO>
}