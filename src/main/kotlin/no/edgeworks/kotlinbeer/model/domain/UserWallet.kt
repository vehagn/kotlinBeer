package no.edgeworks.kotlinbeer.model.domain

import no.edgeworks.kotlinbeer.model.constants.UserPropertyType
import no.edgeworks.kotlinbeer.model.dao.UserDAO
import no.edgeworks.kotlinbeer.model.dao.WalletDAO
import no.edgeworks.kotlinbeer.model.exceptions.WalletUserMismatchException

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
        creditRating = userDAO.userProperties.find { it.type == UserPropertyType.TAB }?.value?.toByte(),
        cashBalance = walletDAO.cashBalance,
        totalSpent = walletDAO.totalSpent,
        latestTransaction = walletDAO.latestTransaction?.let { Transaction(it) }
    ) {
        if (walletDAO.user.id != userDAO.id) {
            throw WalletUserMismatchException()
        }
    }
}