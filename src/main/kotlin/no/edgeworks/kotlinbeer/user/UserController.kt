package no.edgeworks.kotlinbeer.user

import no.edgeworks.kotlinbeer.legacy.LegacyRepository
import no.edgeworks.kotlinbeer.legacy.LegacyUserDAO
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
        return UserDTO(userService.createUser(User(newUser), "new"))
    }

    @PatchMapping("/users")
    fun updateUserDetails(@RequestBody updatedUser: UserDTO): UserDTO {
        return UserDTO(userService.updateUserDetails(User(updatedUser), "changedBy"))
    }

    @PatchMapping("/users/updateCardId")
    fun changeUserCardId(@RequestParam email: String, @RequestParam newCardId: Long): UserDTO {
        return UserDTO(userService.changeUserCardId(email, newCardId, "changedBy"))
    }

    @DeleteMapping("/users/{cardId}")
    fun deleteUser(@PathVariable cardId: Long) {
        userService.deleteUser(cardId)
    }

}