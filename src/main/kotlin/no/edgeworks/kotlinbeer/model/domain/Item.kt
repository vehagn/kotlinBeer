package no.edgeworks.kotlinbeer.model.domain

import no.edgeworks.kotlinbeer.model.dao.ItemDAO
import java.time.ZonedDateTime

class Item(
    val id: Long,
    val name: String,
    val description: String?,
    val createdBy: String,
    val createdDate: ZonedDateTime,
    val changedBy: String?,
    val changedDate: ZonedDateTime?
) {
    constructor(item: ItemDAO) : this(
        id = item.id,
        name = item.name,
        description = item.description,
        createdBy = item.createdBy,
        createdDate = item.createdDate,
        changedBy = item.changedBy,
        changedDate = item.changedDate
    )
}