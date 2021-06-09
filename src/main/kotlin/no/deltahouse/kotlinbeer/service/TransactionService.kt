package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.TransactionRepository
import no.deltahouse.kotlinbeer.database.WalletRepository
import no.deltahouse.kotlinbeer.model.constants.UserPropertyType
import no.deltahouse.kotlinbeer.model.dao.TransactionDAO
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.dao.WalletDAO
import no.deltahouse.kotlinbeer.model.domain.UserWallet
import no.deltahouse.kotlinbeer.model.exceptions.InvalidTransactionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    @Autowired val userService: UserService,
    @Autowired val walletRepository: WalletRepository,
    @Autowired val transactionRepository: TransactionRepository,
    @Value("\${transaction.deposit.max:1000}") val maxDepositValue: Int = 1000,
    @Value("\${transaction.purchase.max:500}") val maxPurchaseValue: Int = 500,
    @Value("\${transaction.tab.multiplier:100}") val creditMultiplier: Int = 100
) {

    @Transactional
    fun purchase(cardId: Long, change: Short) {
        val userDAO = userService.getUserDAOByCardId(cardId)
        val userWalletDAO = getOrCreateWalletForUser(userDAO)

        val creditRating = userDAO.userProperties.find { it.type == UserPropertyType.TAB }?.value?.toByte() ?: 0
        val cashBalance = userWalletDAO.cashBalance

        if (change > maxPurchaseValue) {
            throw InvalidTransactionException("Purchase value too high. Max price is ${maxPurchaseValue}.")
        }
        if (change <= 0) {
            throw InvalidTransactionException("Purchase price must be positive.")
        }
        if (cashBalance + creditRating * creditMultiplier < change) {
            throw InvalidTransactionException(
                "Not enough funds to complete purchase. Current balance $cashBalance"
                        + if (creditRating > 0) " with a tab of ${creditRating * creditMultiplier}." else "."
            )
        }
        walletRepository.save(
            userWalletDAO.copy(
                cashBalance = userWalletDAO.cashBalance - change,
                totalSpent = userWalletDAO.totalSpent + change,
                latestTransaction = transactionRepository.save(TransactionDAO(userWalletDAO, (-1 * change).toShort()))
            )
        )
    }

    @Transactional
    fun deposit(cardId: Long, deposit: Short): UserWallet {
        val userDAO = userService.getUserDAOByCardId(cardId)
        val userWalletDAO = getOrCreateWalletForUser(userDAO)

        val cashBalance = userWalletDAO.cashBalance

        if (deposit > maxDepositValue) {
            throw InvalidTransactionException("Deposit value too high. Max deposit is ${maxDepositValue}.")
        }
        if (deposit <= 0) {
            throw InvalidTransactionException("Deposit must be positive.")
        }
        // If this is true it is very like we've run into an integer overflow
        if (cashBalance + deposit < Int.MIN_VALUE + deposit) {
            throw InvalidTransactionException("Depositing more would result in an integer overflow.")
        }
        val wallet = walletRepository.save(
            userWalletDAO.copy(
                cashBalance = userWalletDAO.cashBalance + deposit,
                latestTransaction = transactionRepository.save(TransactionDAO(userWalletDAO, deposit))
            )
        )
        return UserWallet(userDAO, wallet)
    }

    fun getOrCreateWalletForUser(userDAO: UserDAO): WalletDAO {
        val userWalletDAO = walletRepository.findByUserId(userDAO.id)
        return if (userWalletDAO.isPresent) {
            userWalletDAO.get()
        } else {
            walletRepository.save(
                WalletDAO(
                    user = userDAO,
                    cashBalance = 0,
                    totalSpent = 0
                )
            )
        }

    }
}