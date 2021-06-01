package no.deltahouse.kotlinbeer.model.dao

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
    val firstName: String,
    val lastName: String,
    val birthday: ZonedDateTime?,
    val username: String,
    val studprog: String,
    val isMember: Boolean,
    val tab: Byte,
    val cashBalance: Int,
    val totalSpent: Int,
    @OneToOne
    val latestTransaction: TransactionDAO?,
    //@CreationTimestamp
    val created: ZonedDateTime = ZonedDateTime.now(),
    @UpdateTimestamp
    val changed: ZonedDateTime? = null,
) : Serializable