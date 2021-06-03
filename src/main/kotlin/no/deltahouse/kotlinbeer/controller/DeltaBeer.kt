package no.deltahouse.kotlinbeer.controller

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.model.dao.LegacyUserDAO
import no.deltahouse.kotlinbeer.model.dto.UserDTO
import no.deltahouse.kotlinbeer.service.TransactionService
import no.deltahouse.kotlinbeer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class DeltaBeer(
    @Autowired val userService: UserService,
    @Autowired val legacyRepository: LegacyRepository,
    @Autowired val transactionService: TransactionService
) {
    @GetMapping("/legacy")
    fun getLegacy(): List<LegacyUserDAO> {
        return legacyRepository.getUsers()
    }

    @GetMapping("/new")
    fun getNew(): List<UserDTO> {
        return userService.getAll().map { UserDTO(it) }
    }

    @GetMapping("/user/{id}")
    fun getUserById(@PathVariable id: Long): UserDTO {
        return UserDTO(userService.getByCardId(id))
    }

    @PostMapping("/user/{id}/buy")
    fun buy(@PathVariable id: Long, @RequestParam value: Int): UserDTO {
        return UserDTO(transactionService.purchase(id, value))
    }

    @PostMapping("/user/{id}/deposit")
    fun deposit(@PathVariable id: Long, @RequestParam value: Int): UserDTO {
        return UserDTO(transactionService.deposit(id, value))
    }
}