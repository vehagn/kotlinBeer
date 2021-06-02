package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.database.UserPropertyRepository
import no.deltahouse.kotlinbeer.database.UserRepository
import no.deltahouse.kotlinbeer.model.constants.UserPropertyType
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
    @Autowired val userPropertyRepository: UserPropertyRepository,
    @Autowired val legacyRepository: LegacyRepository
) {
    init {
        try {
            this.migrate()
        } catch (e: Exception) {
            System.out.println(e)
        }
    }

    private fun migrate() {
        val legacyUsers = legacyRepository.getPeople()
        val users = legacyUsers
            .map { leg ->
                UserDAO(
                    -1,
                    leg.cardId,
                    leg.firstName,
                    leg.lastName,
                    when {
                        leg.birthday == "unknown" -> null
                        Integer.parseInt(leg.birthday, 4, 6, 10) > 50 -> ZonedDateTime.parse(
                            leg.birthday + " - 00:00:00 Europe/Oslo",
                            DateTimeFormatter.ofPattern("ddMMyy - HH:mm:ss z")
                        ).minusYears(100)
                        else -> ZonedDateTime.parse(
                            leg.birthday + " - 00:00:00 Europe/Oslo",
                            DateTimeFormatter.ofPattern("ddMMyy - HH:mm:ss z")
                        )
                    },
                    leg.username,
                    leg.studprog,
                    leg.membership == 1,
                    leg.tab.toByte(),
                    leg.cash,
                    leg.spent,
                    listOf(UserPropertyDAO(-1, UserPropertyType.TITLE, leg.comment)),
                    null,
                    ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(leg.creationDate.toLong()),
                        ZoneId.of("Europe/Oslo")
                    ),
                    null
                )
            }
        userRepository.saveAll(users)

        //val userProperties = legacyUsers
        //    .filter { leg -> leg.comment.isNotBlank() }
        //    .map { leg ->
        //        UserPropertyDAO(
        //            -1,
        //            userRepository.findByCardId(leg.cardId).get(),
        //            UserPropertyType.TITLE,
        //            leg.comment
        //        )
        //    }
        //userPropertyRepository.saveAll(userProperties)
    }
}