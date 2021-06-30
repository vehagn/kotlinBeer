package no.edgeworks.kotlinbeer.controller

import no.edgeworks.kotlinbeer.model.dto.UserWalletDTO
import no.edgeworks.kotlinbeer.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
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