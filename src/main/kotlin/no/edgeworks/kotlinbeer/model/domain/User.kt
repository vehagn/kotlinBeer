package no.edgeworks.kotlinbeer.model.domain

import no.edgeworks.kotlinbeer.model.constants.UserPropertyType
import no.edgeworks.kotlinbeer.model.dao.UserDAO
import no.edgeworks.kotlinbeer.model.dto.UserDTO
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
    constructor(userDAO: UserDAO) : this(
        userDAO.cardId,
        userDAO.firstName,
        userDAO.lastName,
        userDAO.birthday,
        userDAO.email,
        userDAO.userProperties.find { it.type == UserPropertyType.TITLE }?.value,
        userDAO.userProperties.filter { it.type == UserPropertyType.COMMENT }.map { it.value },
        userDAO.studprog,
        userDAO.isMember,
        userDAO.userProperties.find { it.type == UserPropertyType.TAB }?.value?.toByte(),
    )

    constructor(userDTO: UserDTO) : this(
        userDTO.cardId,
        userDTO.firstName,
        userDTO.lastName,
        userDTO.birthday,
        userDTO.username,
        userDTO.title,
        userDTO.comments,
        userDTO.studprog,
        userDTO.isMember,
        userDTO.creditRating,
    )
}