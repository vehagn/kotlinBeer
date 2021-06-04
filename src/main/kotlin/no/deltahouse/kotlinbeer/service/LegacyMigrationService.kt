package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.database.TransactionRepository
import no.deltahouse.kotlinbeer.database.UserRepository
import no.deltahouse.kotlinbeer.model.constants.UserPropertyType
import no.deltahouse.kotlinbeer.model.dao.TransactionDAO
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.dao.UserPropertyDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class LegacyMigrationService(
    @Autowired val userRepository: UserRepository,
    @Autowired val legacyRepository: LegacyRepository,
    @Autowired val transactionRepository: TransactionRepository
) {
    init {
        try {
            this.migrate()
        } catch (e: Exception) {
            System.out.println(e)
        }
    }

    private fun migrate() {
        val legacyUsers = legacyRepository.getUsers()
        val users = legacyUsers
            .map {
                val userProperties = mutableListOf<UserPropertyDAO>()
                if (it.comment.isNotBlank()) userProperties.add(
                    UserPropertyDAO(
                        -1,
                        UserPropertyType.TITLE,
                        it.comment
                    )
                )
                if (it.misc.isNotBlank()) userProperties.add(UserPropertyDAO(-1, UserPropertyType.COMMENT, it.misc))
                UserDAO(
                    -1,
                    it.cardId,
                    it.firstName,
                    it.lastName,
                    when {
                        it.birthday == "unknown" -> null
                        Integer.parseInt(it.birthday, 4, 6, 10) > 50 -> ZonedDateTime.parse(
                            it.birthday + " - 00:00:00 Europe/Oslo",
                            DateTimeFormatter.ofPattern("ddMMyy - HH:mm:ss z")
                        ).minusYears(100)
                        else -> ZonedDateTime.parse(
                            it.birthday + " - 00:00:00 Europe/Oslo",
                            DateTimeFormatter.ofPattern("ddMMyy - HH:mm:ss z")
                        )
                    },
                    it.username,
                    it.studprog,
                    it.membership == 1,
                    if (it.tab > 0) it.tab.toByte() else null,
                    it.cash,
                    it.spent,
                    userProperties,
                    ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(it.creationDate.toLong()),
                        ZoneId.of("Europe/Oslo")
                    ),
                    null
                )
            }
        userRepository.saveAll(users)

        val transactions = legacyUsers
            .filter { !(it.spent == 0 && it.cash == 0) }
            .map {
                TransactionDAO(userRepository.findByCardId(it.cardId).get(), it.cash.toShort())
            }
        val completedTransactions = transactionRepository.saveAll(transactions)

        val userTransactions = completedTransactions.map {
            UserDAO(it.user, it)
        }
        userRepository.saveAll(userTransactions)

    }
}