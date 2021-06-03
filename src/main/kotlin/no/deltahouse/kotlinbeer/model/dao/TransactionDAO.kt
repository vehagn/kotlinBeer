package no.deltahouse.kotlinbeer.model.dao

import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "TRANSACTIONS")
data class TransactionDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_generator")
    val id: Long = -1,
    @ManyToOne(fetch = FetchType.LAZY)
    val user: UserDAO,
    val previousBalance: Int,
    val balanceChange: Int,
    val hash: Long,
    val previousTransactionId: Long? = null,
    @CreationTimestamp
    val transactionDate: ZonedDateTime = ZonedDateTime.now(),
) : Serializable {
    constructor(user: UserDAO, change: Int) : this(
        id = -1,
        user = user,
        previousBalance = user.cashBalance,
        balanceChange = change,
        // 977 is an arbitrary large prime number to avoid collisions
        hash = 977 * (977 * ((user.latestTransaction?.hash
            ?: 0) + 977 * (user.hashCode() + user.id))) + (user.latestTransaction?.hashCode() ?: 0)
    )
}