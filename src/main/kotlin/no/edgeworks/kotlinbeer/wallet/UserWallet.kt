package no.edgeworks.kotlinbeer.wallet

import no.edgeworks.kotlinbeer.exceptions.WalletUserMismatchException
import no.edgeworks.kotlinbeer.transaction.Transaction
import no.edgeworks.kotlinbeer.user.UserDAO
import no.edgeworks.kotlinbeer.user.UserPropertyType

class UserWallet(
    val cardId: Long,
    val name: String,
    val creditRating: Byte?,
    val cashBalance: Int,
    val totalSpent: Int,
    val latestTransaction: Transaction?,
) {
    constructor(userDAO: UserDAO, walletDAO: WalletDAO) : this(
        cardId = userDAO.cardId,
        name = userDAO.firstName + " " + userDAO.lastName,
        creditRating = userDAO.userProperties.find { it.type == UserPropertyType.CREDIT }?.value?.toByte(),
        cashBalance = walletDAO.cashBalance,
        totalSpent = walletDAO.totalSpent,
        latestTransaction = walletDAO.latestTransaction?.let { Transaction(it) }
    ) {
        if (walletDAO.user.id != userDAO.id) {
            throw WalletUserMismatchException()
        }
    }
}