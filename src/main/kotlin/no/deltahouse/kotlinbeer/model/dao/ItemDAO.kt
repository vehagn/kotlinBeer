package no.deltahouse.kotlinbeer.model.dao

import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "ITEMS")
data class ItemDAO(
    val name: String,
    val description: String,
    val created: ZonedDateTime,
    val changed: ZonedDateTime? = null,
    @Id
    @GeneratedValue
    val id: Long = 0,
) : Serializable