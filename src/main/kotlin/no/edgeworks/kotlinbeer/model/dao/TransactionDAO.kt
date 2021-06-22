package no.edgeworks.kotlinbeer.model.dao

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
    val wallet: WalletDAO,
    val previousBalance: Int,
    val balanceChange: Short,
    val previousTransactionId: Long? = null,
    val hash: Long?,
    @CreationTimestamp
    val transactionDate: ZonedDateTime = ZonedDateTime.now(),
) : Serializable {
    constructor(wallet: WalletDAO, change: Short) : this(
        id = -1,
        wallet = wallet,
        previousBalance = wallet.cashBalance,
        balanceChange = change,
        previousTransactionId = wallet.latestTransaction?.id,
        hash = 977 * (977 * ((wallet.latestTransaction?.hash
            ?: 0) + 977 * wallet.cashBalance) + (wallet.latestTransaction?.transactionDate.hashCode())),
    )
}