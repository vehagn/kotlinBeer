package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.constants.UserPropertyType
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import java.time.ZonedDateTime

class User(val user: UserDAO) {
    val cardId: Long = user.cardId
    val firstName: String = user.firstName
    val lastName: String = user.lastName
    val username: String = user.username
    val title: String? = user.userProperties.find { it.property == UserPropertyType.TITLE }?.value
    val comments: List<String?> =
        user.userProperties.filter { it.property == UserPropertyType.COMMENT }.map { it.value }
    val studprog: String? = user.studprog
    val isMember: Boolean = user.isMember
    val creditRating: Byte? = user.creditRating
    val cashBalance: Int = user.cashBalance
    val totalSpent: Int = user.totalSpent
    val created: ZonedDateTime = user.created
    val changed: ZonedDateTime? = user.changed
}