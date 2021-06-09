package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.dao.TransactionDAO
import no.deltahouse.kotlinbeer.model.dao.UserWalletDAO
import java.time.ZonedDateTime

class Transaction(transaction: TransactionDAO) {
    val id: Long = transaction.id
    val userWallet: UserWalletDAO = transaction.userWallet
    val previousBalance: Int = transaction.previousBalance
    val balanceChange: Short = transaction.balanceChange
    val transactionDate: ZonedDateTime = transaction.transactionDate
}