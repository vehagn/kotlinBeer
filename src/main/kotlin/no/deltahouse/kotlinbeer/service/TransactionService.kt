package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.TransactionRepository
import no.deltahouse.kotlinbeer.database.UserWalletRepository
import no.deltahouse.kotlinbeer.model.constants.UserPropertyType
import no.deltahouse.kotlinbeer.model.dao.TransactionDAO
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.dao.UserWalletDAO
import no.deltahouse.kotlinbeer.model.exceptions.InvalidTransactionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    @Autowired val userService: UserService,
    @Autowired val userWalletRepository: UserWalletRepository,
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
        val transaction = transactionRepository.save(TransactionDAO(userWalletDAO, (-1 * change).toShort()))
        userWalletRepository.save(
            userWalletDAO.copy(
                cashBalance = userWalletDAO.cashBalance + transaction.balanceChange,
                totalSpent = userWalletDAO.totalSpent + transaction.balanceChange,
                latestTransaction = transaction
            )
        )
    }

    @Transactional
    fun deposit(cardId: Long, deposit: Short) {
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
        val transaction = transactionRepository.save(TransactionDAO(userWalletDAO, deposit))
        userWalletRepository.save(
            userWalletDAO.copy(
                cashBalance = userWalletDAO.cashBalance + transaction.balanceChange,
                latestTransaction = transaction
            )
        )
    }

    fun getOrCreateWalletForUser(userDAO: UserDAO): UserWalletDAO {
        val userWalletQuery = userWalletRepository.findByUserId(userDAO.id)
        return if (userWalletQuery.isPresent) {
            userWalletQuery.get()
        } else {
            userWalletRepository.save(
                UserWalletDAO(
                    user = userDAO,
                    cashBalance = 0,
                    totalSpent = 0
                )
            )
        }

    }
}