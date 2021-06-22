package no.edgeworks.kotlinbeer.database

import no.edgeworks.kotlinbeer.model.dao.UserPropertyDAO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPropertyRepository : JpaRepository<UserPropertyDAO, Long>