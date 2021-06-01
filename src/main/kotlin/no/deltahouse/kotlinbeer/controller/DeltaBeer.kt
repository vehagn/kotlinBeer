package no.deltahouse.kotlinbeer.controller

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.model.dao.LegacyUserDAO
import no.deltahouse.kotlinbeer.model.dao.UserDAO
import no.deltahouse.kotlinbeer.model.dto.UserDTO
import no.deltahouse.kotlinbeer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DeltaBeer(@Autowired val userService: UserService, @Autowired val legacyRepository: LegacyRepository) {
    @GetMapping("/legacy")
    fun getLegacy(): List<LegacyUserDAO> {
        return legacyRepository.getPeople()
    }

    @GetMapping("/new")
    fun getNew(): List<UserDTO> {
        return userService.findAll().stream()
            .map { domain -> UserDTO(domain) }
            .toList()
    }

    @GetMapping("/people/{id}")
    fun getPersonById(@PathVariable id: Long): UserDAO {
        return userService.find(id)
    }
}