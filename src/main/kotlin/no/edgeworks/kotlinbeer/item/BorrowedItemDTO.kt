package no.edgeworks.kotlinbeer.item

import java.io.Serializable
import java.time.ZonedDateTime

class BorrowedItemDTO(
    val id: Long,
    val item: ItemDTO,
    val comment: String?,
    val borrowedDate: ZonedDateTime,
    val returnedDate: ZonedDateTime?,
    val status: Status
) : Serializable {
    constructor(item: BorrowedItem) : this(
        id = item.id,
        item = ItemDTO(item.item),
        comment = item.comment,
        borrowedDate = item.borrowedDate,
        returnedDate = item.returnedDate,
        status = getItemBorrowStatus(item.borrowedDate, item.returnByDate, item.returnedDate)
    )

    companion object {
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

}
