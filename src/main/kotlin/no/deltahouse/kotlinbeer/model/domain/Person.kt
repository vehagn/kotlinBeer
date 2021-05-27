package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.dao.PersonDAO
import java.time.ZonedDateTime

class Person(person: PersonDAO) {
    val cardId: Long = person.cardId
    val firstName:String = person.firstName
    val lastName:String = person.lastName
    val username:String = person.username
    val studprog:String = person.studprog
    val isMember: Boolean = person.isMember
    val userlevel: Byte = person.userlevel
    val password: String = person.password
    val tab: Byte = person.tab
    val cashBalance: Int = person.cashBalance
    val totalSpent: Int = person.totalSpent
    val latestTransaction: Int = person.latestTransaction.hashCode()
    val title: String = person.title
    val comment: String = person.comment
    val created: ZonedDateTime = person.created
    val changed: ZonedDateTime? = person.changed
}