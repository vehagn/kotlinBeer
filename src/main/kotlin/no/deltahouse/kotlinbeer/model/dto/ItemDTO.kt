package no.deltahouse.kotlinbeer.model.dto

import no.deltahouse.kotlinbeer.model.domain.Item
import java.io.Serializable
import java.time.ZonedDateTime

class ItemDTO(item: Item) : Serializable {
    val id: Long = item.id
    val name: String = item.name
    val description: String = item.description
    val created: ZonedDateTime = item.created
    val changed: ZonedDateTime? = item.changed
}