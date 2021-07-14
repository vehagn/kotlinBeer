package no.edgeworks.kotlinbeer.model.dto

import no.edgeworks.kotlinbeer.model.domain.Item
import java.io.Serializable
import java.time.ZonedDateTime

class ItemDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val createdBy: String,
    val createdDate: ZonedDateTime,
    val changedBy: String?,
    val changedDate: ZonedDateTime?
) : Serializable {
    constructor(item: Item) : this(
        id = item.id,
        name = item.name,
        description = item.description,
        createdBy = item.createdBy,
        createdDate = item.createdDate,
        changedBy = item.changedBy,
        changedDate = item.changedDate
    )
}