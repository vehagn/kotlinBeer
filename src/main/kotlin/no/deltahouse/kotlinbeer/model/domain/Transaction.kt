package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.dao.TransactionDAO
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import java.time.ZonedDateTime

class Transaction(transaction: TransactionDAO) {
    val id: Long = transaction.id
    val user: UserDAO = transaction.user
    val previousBalance: Int = transaction.previousBalance
    val balanceChange: Int = transaction.balanceChange
    val currentBalance: Int = transaction.currentBalance
    val transactionDate: ZonedDateTime = transaction.transactionDate
    val previousTransactionHash: Int? = transaction.previousTransaction?.hash
}