package no.edgeworks.kotlinbeer.model.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Card ID is already registered")
class CardIsAlreadyRegisteredException : RuntimeException()