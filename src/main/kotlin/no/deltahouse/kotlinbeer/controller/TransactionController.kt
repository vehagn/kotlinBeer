package no.deltahouse.kotlinbeer.controller

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
    @PostMapping("/users/{id}/buy")
    fun purchase(@PathVariable id: Long, @RequestParam value: Short) {
        transactionService.purchase(id, value)
    }

    @PostMapping("/users/{id}/deposit")
    fun deposit(@PathVariable id: Long, @RequestParam value: Short) {
        transactionService.deposit(id, value)
    }
}