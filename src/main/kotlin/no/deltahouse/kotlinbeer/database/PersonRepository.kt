package no.deltahouse.kotlinbeer.database

import no.deltahouse.kotlinbeer.model.dao.UserDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository: JpaRepository<UserDAO, Long>{
}