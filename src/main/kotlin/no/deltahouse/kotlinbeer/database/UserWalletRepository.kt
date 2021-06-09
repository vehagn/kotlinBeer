package no.deltahouse.kotlinbeer.database

import no.deltahouse.kotlinbeer.model.dao.UserWalletDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserWalletRepository : JpaRepository<UserWalletDAO, Long> {
    fun findByUserId(userId: Long): Optional<UserWalletDAO>
}