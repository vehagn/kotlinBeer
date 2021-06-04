package no.deltahouse.kotlinbeer.controller

import no.deltahouse.kotlinbeer.model.dto.UserDTO
import no.deltahouse.kotlinbeer.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController(
    @Autowired val transactionService: TransactionService,
) {
    @PostMapping("/user/{id}/buy")
    fun purchase(@PathVariable id: Long, @RequestParam value: Short): UserDTO {
        return UserDTO(transactionService.purchase(id, value))
    }

    @PostMapping("/user/{id}/deposit")
    fun deposit(@PathVariable id: Long, @RequestParam value: Short): UserDTO {
        return UserDTO(transactionService.deposit(id, value))
    }
}