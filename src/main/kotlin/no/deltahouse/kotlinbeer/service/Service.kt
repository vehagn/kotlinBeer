package no.deltahouse.kotlinbeer.service

import no.deltahouse.kotlinbeer.database.*
import no.deltahouse.kotlinbeer.model.dao.BorrowedItemDAO
import no.deltahouse.kotlinbeer.model.dao.ItemDAO
import no.deltahouse.kotlinbeer.model.dao.PersonDAO
import no.deltahouse.kotlinbeer.model.domain.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class Service(@Autowired val personRepository: PersonRepository,
              @Autowired val itemRepository: ItemRepository,
              @Autowired val borrowedItemRepository: BorrowedItemRepository,
              @Autowired val transactionRepository: TransactionRepository,
              @Autowired val legacyRepository: LegacyRepository) {
    init {
        this.migrate()
        this.createItems()
    }

    fun findAll(): List<Person> {
        return personRepository.findAll()
            .map { dao -> Person(dao) }
    }

    fun find(id: Long): PersonDAO {
        return personRepository.findById(id).orElse(null);
    }

    private fun migrate() {
        val legacyPersons = legacyRepository.getPeople();
        val persons = legacyPersons
            .map { leg -> PersonDAO(leg) }
        personRepository.saveAll(persons)
    }

    private fun createItems() {
        itemRepository.save(ItemDAO(name = "Item", description = "Big and round", created = ZonedDateTime.now()))
        itemRepository.save(ItemDAO(name = "Item2", description = "Small", created = ZonedDateTime.now()))
        itemRepository.save(ItemDAO(name = "Item3", description = "Big", created = ZonedDateTime.now()))

        val item = itemRepository.getOne(1)
        val borrower = personRepository.getOne(111)
        borrowedItemRepository.save(BorrowedItemDAO(item = item, comment = "comment", borrower = borrower, borrowedDate = ZonedDateTime.now(), returnByDate = ZonedDateTime.now().plusDays(2)))
    }

    @Transactional
    fun buy(id: Long, amount: Int) {
        val person = personRepository.findById(id).orElseGet(null);
        if (person != null) {
            //person.buy(amount);
            personRepository.save(person)
        }
    }
}