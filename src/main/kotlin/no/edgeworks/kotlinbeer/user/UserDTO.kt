package no.edgeworks.kotlinbeer.user

import java.time.ZonedDateTime

data class UserDTO(
    val cardId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthday: ZonedDateTime?,
    val title: String?,
    val comments: Set<String>,
    val userGroup: String?,
    val isMember: Boolean,
    val creditRating: Byte?
) {
    constructor(user: User) : this(
        cardId = user.cardId,
        firstName = user.firstName,
        lastName = user.lastName,
        birthday = user.birthday,
        email = user.email,
        title = user.title,
        comments = user.comments,
        userGroup = user.userGroup,
        isMember = user.isMember,
        creditRating = user.creditRating
    )
}