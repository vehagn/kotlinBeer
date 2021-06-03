package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.TransactionRepository
import no.deltahouse.kotlinbeer.database.UserRepository
import no.deltahouse.kotlinbeer.model.dao.TransactionDAO
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.domain.User
import no.deltahouse.kotlinbeer.model.exceptions.InvalidTransactionException
import no.deltahouse.kotlinbeer.model.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    @Autowired val userRepository: UserRepository,
    @Autowired val transactionRepository: TransactionRepository,
    @Value("\${transaction.deposit.max:1000}") val maxDepositValue: Int,
    @Value("\${transaction.purchase.max:500}") val maxPurchaseValue: Int,
    @Value("\${transaction.tab.multiplier:100}") val tabMultiplier: Int
) {

    @Transactional
    fun purchase(id: Long, change: Int): User {
        val userDAO = userRepository.findByCardId(id)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        val user = userDAO.get()
        if (change > maxPurchaseValue) {
            throw InvalidTransactionException("Purchase value too high. Max price is ${maxPurchaseValue}.")
        }
        if (change < 0) {
            throw InvalidTransactionException("Purchase price can't be negative.")
        }
        if (user.cashBalance + (user.tab ?: 0) * tabMultiplier < change) {
            throw InvalidTransactionException(
                "Not enough funds to complete purchase. Current balance ${user.cashBalance}"
                        + if (user.tab != null) " with a tab of ${user.tab * tabMultiplier}." else "."
            )
        }
        val transaction = transactionRepository.save(TransactionDAO(user, -change))
        return User(userRepository.save(UserDAO(user, transaction)))
    }

    @Transactional
    fun deposit(id: Long, deposit: Int): User {
        val userDAO = userRepository.findByCardId(id)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        val user = userDAO.get();
        if (deposit > maxDepositValue) {
            throw InvalidTransactionException("Deposit value too high. Max deposit is ${maxDepositValue}.")
        }
        if (deposit < 0) {
            throw InvalidTransactionException("Deposit can't be negative.")
        }
        val transaction = transactionRepository.save(TransactionDAO(user, deposit))
        return User(userRepository.save(UserDAO(user, transaction)))
    }
}