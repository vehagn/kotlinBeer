package no.edgeworks.kotlinbeer.model.domain

import no.edgeworks.kotlinbeer.model.dao.BorrowedItemDAO
import java.time.ZonedDateTime

class BorrowedItem(borrowedItem: BorrowedItemDAO) {
    val id: Long = borrowedItem.id
    val item: Item = Item(borrowedItem.item)
    val comment: String? = borrowedItem.comment
    val borrowedDate: ZonedDateTime = borrowedItem.borrowedDate
    val returnByDate: ZonedDateTime = borrowedItem.borrowedDate
    val returnedDate: ZonedDateTime? = if (borrowedItem.returnedDate == null) borrowedItem.returnedDate else null
}