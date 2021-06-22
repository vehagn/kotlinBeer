package no.edgeworks.kotlinbeer.controller

import no.edgeworks.kotlinbeer.model.dto.UserWalletDTO
import no.edgeworks.kotlinbeer.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionController(
    @Autowired val transactionService: TransactionService,
) {
    @PostMapping("/users/{id}/buy")
    fun purchase(@PathVariable id: Long, @RequestParam value: Short) {
        transactionService.purchase(id, value)
    }

    @PostMapping("/users/{id}/deposit")
    fun deposit(@PathVariable id: Long, @RequestParam value: Short): UserWalletDTO {
        return UserWalletDTO(transactionService.deposit(id, value))
    }
}