package no.deltahouse.kotlinbeer.model.dto

import no.deltahouse.kotlinbeer.model.domain.User
import java.time.ZonedDateTime

class UserDTO(user: User) {
    val cardId: Long = user.cardId
    val firstName: String = user.firstName
    val lastName: String = user.lastName
    val username: String = user.username
    val studprog: String = user.studprog
    val membership: Boolean = user.isMember
    val tab: Byte = user.tab
    val cash: Int = user.cashBalance
    val spent: Int = user.totalSpent
    val created: ZonedDateTime = user.created
    val changed: ZonedDateTime? = user.changed
}