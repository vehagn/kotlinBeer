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
    @Value("\${transaction.deposit.max:1000}") val maxDepositValue: Int = 1000,
    @Value("\${transaction.purchase.max:500}") val maxPurchaseValue: Int = 500,
    @Value("\${transaction.tab.multiplier:100}") val creditMultiplier: Int = 100
) {

    @Transactional
    fun purchase(id: Long, change: Short): User {
        val userDAO = userRepository.findByCardId(id)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        val user = userDAO.get()
        if (change > maxPurchaseValue) {
            throw InvalidTransactionException("Purchase value too high. Max price is ${maxPurchaseValue}.")
        }
        if (change <= 0) {
            throw InvalidTransactionException("Purchase price must be positive.")
        }
        if (user.cashBalance + (user.creditRating ?: 0) * creditMultiplier < change) {
            throw InvalidTransactionException(
                "Not enough funds to complete purchase. Current balance ${user.cashBalance}"
                        + if (user.creditRating != null) " with a tab of ${user.creditRating * creditMultiplier}." else "."
            )
        }
        val transaction = transactionRepository.save(TransactionDAO(user, (-1 * change).toShort()))
        return User(userRepository.save(UserDAO(user, transaction)))
    }

    @Transactional
    fun deposit(id: Long, deposit: Short): User {
        val userDAO = userRepository.findByCardId(id)
        if (userDAO.isEmpty) {
            throw UserNotFoundException()
        }
        val user = userDAO.get()
        System.out.println(user.cashBalance + deposit)
        if (deposit > maxDepositValue) {
            throw InvalidTransactionException("Deposit value too high. Max deposit is ${maxDepositValue}.")
        }
        if (deposit <= 0) {
            throw InvalidTransactionException("Deposit must be positive.")
        }
        // If this is true it is very like we've run into an integer overflow
        if (user.cashBalance + deposit < Int.MIN_VALUE + deposit) {
            throw InvalidTransactionException("Depositing more would result in an integer overflow.")
        }
        val transaction = transactionRepository.save(TransactionDAO(user, deposit))
        return User(userRepository.save(UserDAO(user, transaction)))
    }
}