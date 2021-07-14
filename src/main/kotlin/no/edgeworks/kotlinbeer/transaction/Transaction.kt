package no.edgeworks.kotlinbeer.transaction

import java.time.ZonedDateTime

class Transaction(
    val walletId: Long,
    val previousBalance: Int,
    val balanceChange: Short,
    val transactionDate: ZonedDateTime,
) {
    constructor(transaction: TransactionDAO) : this(
        walletId = transaction.wallet.id,
        previousBalance = transaction.previousBalance,
        balanceChange = transaction.balanceChange,
        transactionDate = transaction.transactionDate
    )
}