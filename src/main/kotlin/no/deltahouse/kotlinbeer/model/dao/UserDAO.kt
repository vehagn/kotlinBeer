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
    @Column(length = 63, nullable = false)
    val firstName: String,
    @Column(length = 63, nullable = false)
    val lastName: String,
    val birthday: ZonedDateTime?,
    @Column(length = 31, nullable = false)
    val username: String,
    @Column(length = 31, nullable = true)
    val studprog: String?,
    val isMember: Boolean,
    val creditRating: Byte?,
    val cashBalance: Int,
    val totalSpent: Int,
    @OneToMany(cascade = [CascadeType.ALL])
    @OrderBy(value = "changed DESC")
    val userProperties: List<UserPropertyDAO>,
    //@CreationTimestamp
    val created: ZonedDateTime = ZonedDateTime.now(),
    @UpdateTimestamp
    val changed: ZonedDateTime? = null,
) : Serializable {
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var latestTransaction: TransactionDAO? = null

    constructor(user: UserDAO, transaction: TransactionDAO) : this(
        user.id,
        user.cardId,
        user.firstName,
        user.lastName,
        user.birthday,
        user.username,
        user.studprog,
        user.isMember,
        user.creditRating,
        user.cashBalance + transaction.balanceChange,
        user.totalSpent + if (transaction.balanceChange > 0) transaction.balanceChange else 0,
        user.userProperties,
        user.created,
    ) {
        this.latestTransaction = transaction
    }
}