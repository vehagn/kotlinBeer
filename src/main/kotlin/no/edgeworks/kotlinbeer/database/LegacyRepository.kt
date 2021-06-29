package no.edgeworks.kotlinbeer.database

import no.edgeworks.kotlinbeer.model.dao.LegacyUserDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class LegacyRepository(@Autowired val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun getUsers(): List<LegacyUserDAO> {
        val sqlQuery = "SELECT * FROM legacy_users"
        return jdbcTemplate.query(sqlQuery, DataClassRowMapper.newInstance(LegacyUserDAO::class.java))
    }
}