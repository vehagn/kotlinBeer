package no.deltahouse.kotlinbeer.controller

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.model.dao.LegacyPersonDAO
import no.deltahouse.kotlinbeer.model.dao.PersonDAO
import no.deltahouse.kotlinbeer.model.dto.PersonDTO
import no.deltahouse.kotlinbeer.service.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.w3c.dom.ls.LSInput

@RestController
class DeltaBeer(@Autowired val service: Service, @Autowired val legacyRepository: LegacyRepository) {
    @GetMapping("/legacy")
    fun getLegacy(): List<LegacyPersonDAO> {
        return legacyRepository.getPeople()
    }

    @GetMapping("/new")
    fun getNew(): List<PersonDTO> {
        return service.findAll().stream()
            .map { domain -> PersonDTO(domain) }
            .toList()
    }

    @GetMapping("/people/{id}")
    fun getPersonById(@PathVariable id: Long): PersonDAO {
        val person = service.find(id);
        return person
    }
}