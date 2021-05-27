package no.deltahouse.kotlinbeer.model.dao

import java.io.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "USERS")
data class UserDAO(
    val cardId: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val studprog: String,
    val isMember: Boolean,
    val userlevel: Byte,
    val password: String,
    val tab: Byte,
    val cashBalance: Int,
    val totalSpent: Int,
    @OneToOne
    val latestTransaction: TransactionDAO?,
    val title: String,
    val comment: String,
    val created: ZonedDateTime,
    val changed: ZonedDateTime?,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    val id: Long = 0,
) : Serializable {
    constructor(legacyUserDAO: LegacyUserDAO) : this(
        legacyUserDAO.cardId,
        legacyUserDAO.firstName,
        legacyUserDAO.lastName,
        legacyUserDAO.username,
        legacyUserDAO.studprog,
        legacyUserDAO.membership == 1,
        legacyUserDAO.userlevel.toByte(),
        legacyUserDAO.password,
        legacyUserDAO.tab.toByte(),
        legacyUserDAO.cash,
        legacyUserDAO.spent,
        null,
        legacyUserDAO.comment,
        legacyUserDAO.misc,
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(legacyUserDAO.creationDate.toLong()), ZoneId.of("Europe/Oslo")),
        null
    )
}