package no.edgeworks.kotlinbeer.wallet

import no.edgeworks.kotlinbeer.transaction.Transaction

class UserWalletDTO(
    val cardId: Long,
    val name: String,
    val creditRating: Byte?,
    val cashBalance: Int,
    val totalSpent: Int,
    val latestTransaction: LatestTransaction?
) {
    constructor(userWallet: UserWallet) : this(
        cardId = userWallet.cardId,
        name = userWallet.name,
        creditRating = userWallet.creditRating,
        cashBalance = userWallet.cashBalance,
        totalSpent = userWallet.totalSpent,
        latestTransaction = userWallet.latestTransaction?.let { LatestTransaction(it) }
    )

    companion object {
        class LatestTransaction(transaction: Transaction) {
            val previousBalance = transaction.previousBalance
            val balanceChange = transaction.balanceChange
            val transactionDate = transaction.transactionDate
        }
    }
}