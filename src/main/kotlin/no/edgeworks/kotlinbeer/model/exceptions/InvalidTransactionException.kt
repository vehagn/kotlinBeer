package no.edgeworks.kotlinbeer.model.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class InvalidTransactionException(override val message: String?) : RuntimeException(message)