package no.deltahouse.kotlinbeer.model.domain

import no.deltahouse.kotlinbeer.model.dao.ItemDAO
import java.time.ZonedDateTime

class Item(item: ItemDAO) {
    val id: Long = item.id
    val name: String = item.name
    val description: String? = item.description
    val createdBy: String = item.createdBy
    val createdDate: ZonedDateTime = item.createdDate
    val changedBy: String? = item.changedBy
    val changedDate: ZonedDateTime? = item.changedDate
}