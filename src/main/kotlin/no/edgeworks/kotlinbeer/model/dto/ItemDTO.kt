package no.edgeworks.kotlinbeer.model.dto

import no.edgeworks.kotlinbeer.model.domain.Item
import java.io.Serializable
import java.time.ZonedDateTime

class ItemDTO(item: Item) : Serializable {
    val id: Long = item.id
    val name: String = item.name
    val description: String? = item.description
    val createdBy: String = item.createdBy
    val createdDate: ZonedDateTime = item.createdDate
    val changedBy: String? = item.changedBy
    val changedDate: ZonedDateTime? = item.changedDate
}