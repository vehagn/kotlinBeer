package no.deltahouse.kotlinbeer.model.dao

import no.deltahouse.kotlinbeer.model.constants.UserPropertyType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "USER_PROPERTIES")
data class UserPropertyDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_property_id_generator")
    val id: Long = -1,
    @Enumerated(EnumType.STRING)
    @Type(type = "no.deltahouse.kotlinbeer.database.PostgreSQLEnumType")
    val property: UserPropertyType,
    val value: String,
    @CreationTimestamp
    val created: ZonedDateTime = ZonedDateTime.now(),
    @UpdateTimestamp
    val changed: ZonedDateTime? = null,
) : Serializable