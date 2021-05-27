package no.deltahouse.kotlinbeer.database

import no.deltahouse.kotlinbeer.model.dao.BorrowedItemDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BorrowedItemRepository : JpaRepository<BorrowedItemDAO, Long>