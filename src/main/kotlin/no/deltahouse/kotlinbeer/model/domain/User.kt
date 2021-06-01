package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.dao.UserDAO
import java.time.ZonedDateTime

class User(user: UserDAO) {
    val cardId: Long = user.cardId
    val firstName: String = user.firstName
    val lastName: String = user.lastName
    val username: String = user.username
    val studprog: String = user.studprog
    val isMember: Boolean = user.isMember
    val tab: Byte = user.tab
    val cashBalance: Int = user.cashBalance
    val totalSpent: Int = user.totalSpent
    val latestTransactionHash: Long? = user.latestTransaction?.hash
    val created: ZonedDateTime = user.created
    val changed: ZonedDateTime? = user.changed
}