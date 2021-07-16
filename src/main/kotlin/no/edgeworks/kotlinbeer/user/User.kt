package no.edgeworks.kotlinbeer.user

import no.edgeworks.kotlinbeer.exceptions.UserIsDeletedException
import java.time.ZonedDateTime

data class User(
    val cardId: Long,
    val firstName: String,
    val lastName: String,
    val birthday: ZonedDateTime?,
    val email: String,
    val title: String?,
    val comments: Set<String>,
    val userGroup: String?,
    val isMember: Boolean,
    val creditRating: Byte?,
) {
    constructor(userDAO: UserDAO) : this(
        userDAO.cardId,
        userDAO.firstName,
        userDAO.lastName,
        userDAO.birthday,
        userDAO.email,
        userDAO.userProperties.find { it.type == UserPropertyType.TITLE }?.value,
        userDAO.userProperties.filter { it.type == UserPropertyType.COMMENT }.mapNotNullTo(HashSet()) { it.value },
        userDAO.userGroup,
        userDAO.isMember,
        userDAO.userProperties.find { it.type == UserPropertyType.CREDIT }?.value?.toByte(),
    ) {
        if (userDAO.deletedDate != null) {
            throw UserIsDeletedException()
        }
    }

    constructor(userDTO: UserDTO) : this(
        cardId = userDTO.cardId,
        firstName = userDTO.firstName,
        lastName = userDTO.lastName,
        birthday = userDTO.birthday,
        email = userDTO.email,
        title = userDTO.title,
        comments = userDTO.comments,
        userGroup = userDTO.userGroup,
        isMember = userDTO.isMember,
        creditRating = userDTO.creditRating,
    )
}