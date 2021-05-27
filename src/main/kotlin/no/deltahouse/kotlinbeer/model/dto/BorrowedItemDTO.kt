package no.deltahouse.kotlinbeer.model.dto

import no.deltahouse.kotlinbeer.model.domain.BorrowedItem
import java.io.Serializable
import java.time.ZonedDateTime

class BorrowedItemDTO(item: BorrowedItem) : Serializable {
    val id: Long = item.id
    val item: ItemDTO = ItemDTO(item.item)
    val comment: String? = item.comment
    val borrowedDate: ZonedDateTime = item.borrowedDate
    val returnedDate: ZonedDateTime? = item.returnedDate
    val status: Status = getItemBorrowStatus(item.borrowedDate, item.returnByDate, item.returnedDate)

    enum class Status { SCHEDULED, ACTIVE, LATE, RETURNED, DISCREPANCY }

    private fun getItemBorrowStatus(
        borrowedDate: ZonedDateTime,
        returnByDate: ZonedDateTime,
        returnedDate: ZonedDateTime?
    ): Status {
        val now = ZonedDateTime.now()

        if (returnedDate != null) {
            return if (returnedDate.isAfter(now)) Status.RETURNED else Status.DISCREPANCY
        }

        if (borrowedDate.isBefore(now)) return Status.SCHEDULED

        return if (returnByDate.isAfter(now)) Status.ACTIVE else Status.LATE
    }
}