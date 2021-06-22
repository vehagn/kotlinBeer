package no.edgeworks.kotlinbeer.model.dto

import no.edgeworks.kotlinbeer.model.domain.Transaction
import no.edgeworks.kotlinbeer.model.domain.UserWallet

class UserWalletDTO(userWallet: UserWallet) {
    val name = userWallet.name
    val cardId = userWallet.cardId
    val creditRating = userWallet.creditRating
    val cashBalance = userWallet.cashBalance
    val totalSpent = userWallet.totalSpent
    val latestTransaction = userWallet.latestTransaction?.let { LatestTransaction(it) }

    class LatestTransaction(transaction: Transaction) {
        val previousBalance = transaction.previousBalance
        val balanceChange = transaction.balanceChange
        val transactionDate = transaction.transactionDate
    }
}