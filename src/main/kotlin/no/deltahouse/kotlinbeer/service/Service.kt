package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.LegacyRepository
import no.deltahouse.kotlinbeer.database.Repository
import no.deltahouse.kotlinbeer.model.dao.PersonDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors
import kotlin.streams.toList

@Service
class Service(@Autowired val repository: Repository, @Autowired val legacyRepository: LegacyRepository) {
    init {
        this.migrate()
    }

    fun findAll(): List<PersonDAO> {
        return repository.findAll();
    }

    fun find(id: Long): PersonDAO {
        return repository.findById(id).orElse(null);
    }

    private fun migrate() {
        val legacyPersons = legacyRepository.getPeople();
        val persons = legacyPersons.stream()
            .map { leg -> PersonDAO(leg) }
            .toList()
        repository.saveAll(persons)
    }

    @Transactional
    fun buy(id: Long, amount: Int) {
        val person = repository.findById(id).orElseGet(null);
        if (person != null) {
            //person.buy(amount);
            repository.save(person)
        }
    }
}