package no.edgeworks.kotlinbeer.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPropertyRepository : JpaRepository<UserPropertyDAO, Long>