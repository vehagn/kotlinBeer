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
    @ManyToOne
    val user: UserDAO,
    val previousBalance: Int,
    val balanceChange: Int,
    val currentBalance: Int,
    @OneToOne
    val previousTransaction: TransactionDAO?,
    val hash: Long,
    @CreationTimestamp
    val transactionDate: ZonedDateTime = ZonedDateTime.now(),
) : Serializable