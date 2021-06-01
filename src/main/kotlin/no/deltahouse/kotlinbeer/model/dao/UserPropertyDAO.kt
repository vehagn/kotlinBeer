package no.deltahouse.kotlinbeer.model.dao

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "USER_PROPERTIES")
data class UserPropertyDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    val id: Long = -1,
    @ManyToOne
    val user: UserDAO,
    val property: String,
    val value: String,
    @CreationTimestamp
    val created: ZonedDateTime = ZonedDateTime.now(),
    @UpdateTimestamp
    val changed: ZonedDateTime? = null,
) : Serializable