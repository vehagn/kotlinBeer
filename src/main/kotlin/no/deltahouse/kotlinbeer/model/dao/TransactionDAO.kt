package no.deltahouse.kotlinbeer.model.dao

import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "TRANSACTIONS")
data class TransactionDAO(
    @ManyToOne
    val user: UserDAO,
    val previousBalance: Int,
    val balanceChange: Int,
    val currentBalance: Int,
    val transactionDate: ZonedDateTime,
    @OneToOne
    val previousTransaction: TransactionDAO?,
    val hash: Int,
    @Id
    @GeneratedValue
    val id: Long = 0,
) : Serializable