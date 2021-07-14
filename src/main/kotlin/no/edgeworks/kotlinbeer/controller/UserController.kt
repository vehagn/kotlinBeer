package no.edgeworks.kotlinbeer.controller

import no.edgeworks.kotlinbeer.database.LegacyRepository
import no.edgeworks.kotlinbeer.model.dao.LegacyUserDAO
import no.edgeworks.kotlinbeer.model.domain.User
import no.edgeworks.kotlinbeer.model.dto.UserDTO
import no.edgeworks.kotlinbeer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
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
    fun getUserByCardId(@PathVariable cardId: Long): UserDTO {
        return UserDTO(userService.getUserByCardId(cardId))
    }

    @PostMapping("/users")
    fun createUser(@RequestBody newUser: UserDTO): UserDTO {
        userService.createUser(User(newUser), "new")
        return UserDTO(userService.getUserByCardId(newUser.cardId))
    }

    @PatchMapping("/users")
    fun updateUserDetails(@RequestBody updatedUser: UserDTO): UserDTO {
        userService.updateUserDetails(User(updatedUser), "changedBy")
        return UserDTO(userService.getUserByCardId(updatedUser.cardId))
    }

    @PatchMapping("/users/updateCardId")
    fun changeUserCardId(@RequestParam email: String, @RequestParam newCardId: Long): UserDTO {
        userService.changeUserCardId(email, newCardId, "changedBy")
        return UserDTO(userService.getUserByCardId(newCardId))
    }

    @DeleteMapping("/users/{cardId}")
    fun deleteUser(@PathVariable cardId: Long) {
        userService.deleteUser(cardId)
    }

}