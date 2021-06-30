package no.edgeworks.kotlinbeer.model.dto

import no.edgeworks.kotlinbeer.model.domain.User
import java.time.ZonedDateTime

class UserDTO(user: User) {
    val cardId: Long = user.cardId
    val firstName: String = user.firstName
    val lastName: String = user.lastName
    val birthday: ZonedDateTime? = user.birthday
    val email: String = user.email
    val title: String? = user.title
    val comments: List<String?> = user.comments
    val studprog: String? = user.studprog
    val isMember: Boolean = user.isMember
    val creditRating: Byte? = user.creditRating
}