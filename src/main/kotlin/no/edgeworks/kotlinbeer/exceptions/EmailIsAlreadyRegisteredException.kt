package no.edgeworks.kotlinbeer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "E-mail is already registered.")
class EmailIsAlreadyRegisteredException : RuntimeException()