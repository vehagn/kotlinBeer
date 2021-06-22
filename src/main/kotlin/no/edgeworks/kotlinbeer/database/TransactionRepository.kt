package no.edgeworks.kotlinbeer.database

import no.edgeworks.kotlinbeer.model.dao.TransactionDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<TransactionDAO, Long>