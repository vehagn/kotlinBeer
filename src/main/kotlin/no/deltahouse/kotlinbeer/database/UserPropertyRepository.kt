package no.deltahouse.kotlinbeer.database

import no.deltahouse.kotlinbeer.model.dao.UserPropertyDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPropertyRepository : JpaRepository<UserPropertyDAO, Long>