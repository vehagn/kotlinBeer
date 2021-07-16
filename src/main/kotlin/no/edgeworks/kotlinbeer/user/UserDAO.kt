package no.edgeworks.kotlinbeer.user

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "USERS")
data class UserDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    val id: Long = -1,
    @Column(unique = true)
    val cardId: Long,
    @Column(length = 63, nullable = false)
    val firstName: String,
    @Column(length = 63, nullable = false)
    val lastName: String,
    @Column(length = 63, nullable = false)
    val email: String,
    val birthday: ZonedDateTime?,
    @Column(length = 31, nullable = true)
    val userGroup: String?,
    val isMember: Boolean,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy(value = "changed_date DESC")
    val userProperties: Set<UserPropertyDAO>,
    @Column(length = 15, nullable = false)
    val createdBy: String,
    //@CreationTimestamp
    val createdDate: ZonedDateTime = ZonedDateTime.now(),
    @Column(length = 15, nullable = true)
    val changedBy: String? = null,
    @UpdateTimestamp
    val changedDate: ZonedDateTime? = null,
    val deletedDate: ZonedDateTime? = null,
) : Serializable {

    constructor(user: User, createdBy: String) : this(
        -1,
        user.cardId,
        user.firstName,
        user.lastName,
        user.email,
        user.birthday,
        user.userGroup,
        user.isMember,
        getUserProperties(user, createdBy),
        createdBy
    )

    companion object {
        private fun getUserProperties(user: User, createdBy: String): Set<UserPropertyDAO> {
            val userProperties = mutableSetOf<UserPropertyDAO>()
            if (user.title != null) {
                userProperties.add(UserPropertyDAO(UserPropertyType.TITLE, user.title, createdBy))
            }
            if (user.creditRating != null && user.creditRating.toByte() > 0) {
                userProperties.add(UserPropertyDAO(UserPropertyType.CREDIT, user.creditRating.toString(), createdBy))
            }
            user.comments.forEach { userProperties.add(UserPropertyDAO(UserPropertyType.COMMENT, it, createdBy)) }
            return userProperties
        }
    }

}