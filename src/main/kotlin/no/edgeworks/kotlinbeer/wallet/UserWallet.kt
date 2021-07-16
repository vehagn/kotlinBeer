package no.edgeworks.kotlinbeer.wallet

import no.edgeworks.kotlinbeer.exceptions.WalletUserMismatchException
import no.edgeworks.kotlinbeer.user.UserDAO
import no.edgeworks.kotlinbeer.user.UserPropertyType

data class UserWallet(
    val userDAO: UserDAO,
    val walletDAO: WalletDAO,
) {
    init {
        if (walletDAO.user.id != userDAO.id) {
            throw WalletUserMismatchException()
        }
    }

    fun creditRating() = userDAO.userProperties.find { it.type == UserPropertyType.CREDIT }?.value?.toByte() ?: 0
    fun cashBalance() = walletDAO.cashBalance
    fun latestTransaction() = walletDAO.latestTransaction?.let { Transaction(it) }
    fun totalSpent() = walletDAO.totalSpent
    fun cardId() = userDAO.cardId
    fun name() = userDAO.firstName + " " + userDAO.lastName
}