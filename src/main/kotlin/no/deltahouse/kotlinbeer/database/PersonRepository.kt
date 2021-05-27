package no.deltahouse.kotlinbeer.database

import no.deltahouse.kotlinbeer.model.dao.PersonDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository: JpaRepository<PersonDAO, Long>{
}