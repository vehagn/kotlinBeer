package no.edgeworks.kotlinbeer.legacy

import java.io.Serializable
import javax.persistence.Id

data class LegacyUserDAO(
    @Id
    val cardId: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val birthday: String,
    val studprog: String,
    val membership: Int,
    val userlevel: Int,
    val password: String,
    val tab: Int,
    val cash: Int,
    val spent: Int,
    val borrowed: String,
    val comment: String,
    val misc: String,
    val creationDate: Int
) : Serializable
