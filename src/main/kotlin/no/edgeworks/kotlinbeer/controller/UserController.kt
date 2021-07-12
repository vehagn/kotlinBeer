package no.edgeworks.kotlinbeer.controller

import no.edgeworks.kotlinbeer.database.LegacyRepository
import no.edgeworks.kotlinbeer.model.dao.LegacyUserDAO
import no.edgeworks.kotlinbeer.model.domain.User
import no.edgeworks.kotlinbeer.model.dto.UserDTO
import no.edgeworks.kotlinbeer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime

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

        val userDTO = UserDTO(
            cardId = 100L,
            firstName = "FirstName",
            lastName = "LastName",
            email = "test@test.no",
            birthday = ZonedDateTime.now(),
            title = "title",
            comments = listOf("comment"),
            studprog = "BFY",
            isMember = true,
            creditRating = 1
        )

        this.createUser(userDTO)
        return userService.getAllUsers().map { UserDTO(it) }
    }

    @GetMapping("/users/{cardId}")
    fun getUserByCardId(@PathVariable cardId: Long): UserDTO {
        return UserDTO(userService.getUserByCardId(cardId))
    }

    @PatchMapping("/users")
    fun updateUserBasicDetails(@RequestBody updatedUser: UserDTO): UserDTO {
        userService.updateUserBasicDetails(User(updatedUser), "changed")
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