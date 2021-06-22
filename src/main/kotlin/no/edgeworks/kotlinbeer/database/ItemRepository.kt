package no.edgeworks.kotlinbeer.database

import no.edgeworks.kotlinbeer.model.dao.ItemDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<ItemDAO, Long>