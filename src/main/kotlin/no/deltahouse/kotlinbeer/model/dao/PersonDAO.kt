package no.deltahouse.kotlinbeer.model.dao

import java.io.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity(name = "PERSONS")
data class PersonDAO(
    @Id
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
    val changed: ZonedDateTime?
) : Serializable {
    constructor(legacyPersonDAO: LegacyPersonDAO) : this(
        legacyPersonDAO.cardId,
        legacyPersonDAO.firstName,
        legacyPersonDAO.lastName,
        legacyPersonDAO.username,
        legacyPersonDAO.studprog,
        legacyPersonDAO.membership == 1,
        legacyPersonDAO.userlevel.toByte(),
        legacyPersonDAO.password,
        legacyPersonDAO.tab.toByte(),
        legacyPersonDAO.cash,
        legacyPersonDAO.spent,
        null,
        legacyPersonDAO.comment,
        legacyPersonDAO.misc,
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(legacyPersonDAO.creationDate.toLong()), ZoneId.of("Europe/Oslo")),
        null
    )
}