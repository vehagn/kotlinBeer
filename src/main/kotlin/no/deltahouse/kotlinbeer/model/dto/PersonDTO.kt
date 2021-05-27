package no.deltahouse.kotlinbeer.model.dto

import no.deltahouse.kotlinbeer.model.domain.Person
import java.time.ZonedDateTime

class PersonDTO(person: Person) {
    val cardId: Long = person.cardId
    val firstName: String = person.firstName
    val lastName: String = person.lastName
    val username: String = person.username
    val studprog: String = person.studprog
    val membership: Boolean = person.isMember
    val userlevel: Byte = person.userlevel
    val tab: Byte = person.tab
    val cash: Int = person.cashBalance
    val spent: Int = person.totalSpent
    val title: String = person.title
    val comment: String = person.comment
    val created: ZonedDateTime = person.created
    val changed: ZonedDateTime? = person.changed
}