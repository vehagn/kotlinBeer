package no.deltahouse.kotlinbeer.controller

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.model.dao.LegacyUserDAO
import no.deltahouse.kotlinbeer.model.dto.UserDTO
import no.deltahouse.kotlinbeer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    @Autowired val userService: UserService,
    @Autowired val legacyRepository: LegacyRepository,
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

}