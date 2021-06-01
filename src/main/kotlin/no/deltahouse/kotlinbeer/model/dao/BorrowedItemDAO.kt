package no.deltahouse.kotlinbeer.model.dao

import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "BORROWED_ITEMS")
data class BorrowedItemDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "borrowed_item_id_generator")
    val id: Long = -1,
    @ManyToOne
    val item: ItemDAO,
    val comment: String?,
    @ManyToOne
    val borrower: UserDAO,
    val borrowedDate: ZonedDateTime = ZonedDateTime.now(),
    val returnByDate: ZonedDateTime,
    val returnedDate: ZonedDateTime? = null,
) : Serializable