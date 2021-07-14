package no.edgeworks.kotlinbeer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.GONE, reason = "Requested user is deleted.")
class UserIsDeletedException : RuntimeException()