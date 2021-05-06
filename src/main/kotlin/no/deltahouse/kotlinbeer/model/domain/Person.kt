package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.dao.PersonDAO
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class Person(person: PersonDAO) {
    val cardId: Long = person.cardId
    val firstName:String = person.firstName
    val lastName:String = person.lastName
    val username:String = person.username
    val studprog:String = person.studprog
    val membership: Boolean = person.membership
    val userlevel: Byte = person.userlevel
    val password: String = person.password
    val tab: Byte = person.tab
    val cash: Int = person.cash
    val spent: Int = person.spent
    val borrowed: String = person.borrowedItems
    val comment: String = person.comment
    val misc: String = person.miscComment
    val creationDate: ZonedDateTime = ZonedDateTime.ofInstant(person.creationDate, ZoneId.of("Europe/Oslo"))
}