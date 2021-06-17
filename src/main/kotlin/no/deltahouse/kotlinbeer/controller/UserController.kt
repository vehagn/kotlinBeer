package no.deltahouse.kotlinbeer.controller

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.model.dao.LegacyUserDAO
import no.deltahouse.kotlinbeer.model.domain.User
import no.deltahouse.kotlinbeer.model.dto.UserDTO
import no.deltahouse.kotlinbeer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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
        return userService.getAllUsers().map { UserDTO(it) }
    }

    @GetMapping("/users/{cardId}")
    fun getUserById(@PathVariable cardId: Long): UserDTO {
        return UserDTO(userService.getUserByCardId(cardId))
    }

    @PatchMapping("/users")
    fun updateUser(@RequestBody updatedUser: UserDTO): UserDTO {
        userService.updateUser(User(updatedUser), "changed")
        return UserDTO(userService.getUserByCardId(updatedUser.cardId))
    }

    @PostMapping("/users")
    fun createUser(@RequestBody newUser: UserDTO): UserDTO {
        userService.createUser(User(newUser), "new")
        return UserDTO(userService.getUserByCardId(newUser.cardId))
    }

    @DeleteMapping("/users/{cardId}")
    fun deleteUser(@PathVariable cardId: Long) {
        userService.deleteUser(cardId)
    }

}