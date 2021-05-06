package no.deltahouse.kotlinbeer.model.dao

import lombok.EqualsAndHashCode
import lombok.ToString
import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor
import java.io.Serializable
import java.time.Instant
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.Id

@ToString
@EqualsAndHashCode
@Entity(name = "Person")
class PersonDAO(
    @Id
    val cardId: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val studprog: String,
    val membership: Boolean,
    val userlevel: Byte,
    val password: String,
    val tab: Byte,
    val cash: Int,
    val spent: Int,
    val borrowedItems: String,
    val comment: String,
    val miscComment: String,
    val creationDate: Instant
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
        legacyPersonDAO.borrowed,
        legacyPersonDAO.comment,
        legacyPersonDAO.misc,
        Instant.ofEpochSecond(legacyPersonDAO.creationDate.toLong())
    )
}