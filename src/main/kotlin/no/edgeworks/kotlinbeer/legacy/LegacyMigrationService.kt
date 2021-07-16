package no.edgeworks.kotlinbeer.legacy

import no.edgeworks.kotlinbeer.transaction.TransactionDAO
import no.edgeworks.kotlinbeer.transaction.TransactionRepository
import no.edgeworks.kotlinbeer.user.*
import no.edgeworks.kotlinbeer.wallet.WalletDAO
import no.edgeworks.kotlinbeer.wallet.WalletRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class LegacyMigrationService(
    @Autowired val userRepository: UserRepository,
    @Autowired val userService: UserService,
    @Autowired val legacyRepository: LegacyRepository,
    @Autowired val transactionRepository: TransactionRepository,
    @Autowired val walletRepository: WalletRepository
) {
    init {
        try {
            this.migrate()
        } catch (e: Exception) {
            println(e)
        }
    }

    private fun migrate() {
        val legacyUsers = legacyRepository.getUsers()

        val mergedLegacyUsers = mutableMapOf<String, LegacyUserDAO>()
        legacyUsers
            .sortedByDescending { it.creationDate } // Newest first
            .forEach {
                mergedLegacyUsers.computeIfPresent(it.username) { _, v ->
                    v.copy(
                        cash = v.cash + it.cash,
                        spent = v.spent + it.cash,
                        comment = v.comment + it.comment,
                        misc = v.misc + it.misc,
                        creationDate = it.creationDate,
                    )
                }
                mergedLegacyUsers.putIfAbsent(it.username, it)
            }

        val users = mergedLegacyUsers.values
            .map {
                val userProperties = mutableSetOf<UserPropertyDAO>()
                if (it.comment.isNotBlank()) userProperties.add(
                    UserPropertyDAO(
                        type = UserPropertyType.TITLE,
                        value = it.comment,
                        createdBy = "Migration"
                    )
                )
                if (it.misc.isNotBlank()) userProperties.add(
                    UserPropertyDAO(
                        type = UserPropertyType.COMMENT,
                        value = it.misc,
                        createdBy = "Migration"
                    )
                )
                if (it.tab > 0) userProperties.add(
                    UserPropertyDAO(
                        type = UserPropertyType.CREDIT,
                        value = it.tab.toString(),
                        createdBy = "Migration"
                    )
                )
                UserDAO(
                    cardId = it.cardId,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.username + "@stud.ntnu.no",
                    birthday = when {
                        it.birthday == "unknown" -> null
                        Integer.parseInt(it.birthday, 4, 6, 10) > 50 -> ZonedDateTime.parse(
                            it.birthday + " - 12:00:00 UTC",
                            DateTimeFormatter.ofPattern("ddMMyy - HH:mm:ss z")
                        ).minusYears(100)
                        else -> ZonedDateTime.parse(
                            it.birthday + " - 12:00:00 UTC",
                            DateTimeFormatter.ofPattern("ddMMyy - HH:mm:ss z")
                        )
                    },
                    userGroup = it.studprog,
                    isMember = it.membership == 1,
                    userProperties = userProperties,
                    createdBy = "Migration",
                    createdDate = ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(it.creationDate.toLong()),
                        ZoneId.of("Europe/Oslo")
                    ),
                )
            }
        userRepository.saveAll(users)

        mergedLegacyUsers.values
            .filter { !(it.spent == 0 && it.cash == 0) }
            .forEach {
                val userDAO = userService.getUserDAOByCardId(it.cardId)
                val userWalletDAO =
                    walletRepository.save(WalletDAO(user = userDAO, cashBalance = 0, totalSpent = 0))
                val transactionDAO = transactionRepository.save(TransactionDAO(userWalletDAO, it.cash.toShort()))
                walletRepository.save(
                    userWalletDAO.copy(
                        cashBalance = it.cash,
                        totalSpent = it.spent,
                        latestTransaction = transactionDAO
                    )
                )
            }
    }
}