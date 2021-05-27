package no.deltahouse.kotlinbeer.model.dao

import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity(name = "BORROWED_ITEMS")
data class BorrowedItemDAO(
    @ManyToOne
    val item: ItemDAO,
    val comment: String?,
    @ManyToOne
    val borrower: PersonDAO,
    val borrowedDate: ZonedDateTime,
    val returnByDate: ZonedDateTime,
    val returnedDate: ZonedDateTime? = null,
    @Id
    @GeneratedValue
    val id: Long = 0,
) : Serializable