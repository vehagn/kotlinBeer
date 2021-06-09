package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.constants.UserPropertyType
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.dto.UserDTO
import java.time.ZonedDateTime

class User(
    val cardId: Long,
    val firstName: String,
    val lastName: String,
    val birthday: ZonedDateTime?,
    val email: String,
    val title: String?,
    val comments: List<String?>,
    val studprog: String?,
    val isMember: Boolean,
    val creditRating: Byte?,
) {
    constructor(user: UserDAO) : this(
        user.cardId,
        user.firstName,
        user.lastName,
        user.birthday,
        user.email,
        user.userProperties.find { it.type == UserPropertyType.TITLE }?.value,
        user.userProperties.filter { it.type == UserPropertyType.COMMENT }.map { it.value },
        user.studprog,
        user.isMember,
        user.userProperties.find { it.type == UserPropertyType.TAB }?.value?.toByte(),
    )

    constructor(user: UserDTO) : this(
        user.cardId,
        user.firstName,
        user.lastName,
        user.birthday,
        user.username,
        user.title,
        user.comments,
        user.studprog,
        user.isMember,
        user.creditRating,
    )
}