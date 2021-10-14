package at.campus02.dbp2.mappings;

import at.campus02.dbp2.mappings.AccountType;
import at.campus02.dbp2.mappings.Customer;
import at.campus02.dbp2.mappings.CustomerRepository;
import at.campus02.dbp2.mappings.CustomerRepositoryJpa;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerRepositoryCrudSpec {

    @Test
    public void createNullAsCustomerReturnsFalse() {
        // given
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistenceUnitName");
        CustomerRepository repository = new CustomerRepositoryJpa(factory);

        // when
        boolean result = repository.create(null);

        //then
        assertFalse(result);
    }

    @Test
    public void createPersistsCustomerInDatabaseAndReturnsTrue() {
        // given
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistenceUnitName");
        CustomerRepository repository = new CustomerRepositoryJpa(factory);

        String firstname = "Firstname";
        String lastname = "Lastname";
        AccountType accountType = AccountType.BASIC;
        LocalDate registeredSince = LocalDate.now();

        Customer toCreate = new Customer();
        toCreate.setFirstname(firstname);
        toCreate.setLastname(lastname);
        toCreate.setAccountType(accountType);
        toCreate.setRegisteredSince(registeredSince);

        // when
        boolean result = repository.create(toCreate);

        // then
        assertTrue(result);
        // Kontrolle auch in der DB
        EntityManager manager = factory.createEntityManager();
        Customer fromDB = manager.find(Customer.class, toCreate.getId());
        assertEquals(firstname, fromDB.getFirstname());
        assertEquals(lastname, fromDB.getLastname());
        assertEquals(accountType, fromDB.getAccountType());
        assertEquals(registeredSince, fromDB.getRegisteredSince());
    }

    @Test
    public void createExistingCustomerReturnsFalse() {
        // given
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistenceUnitName");
        CustomerRepository repository = new CustomerRepositoryJpa(factory);

        String firstname = "Firstname";
        String lastname = "Lastname";
        AccountType accountType = AccountType.BASIC;
        LocalDate registeredSince = LocalDate.now();

        Customer toCreate = new Customer();
        toCreate.setFirstname(firstname);
        toCreate.setLastname(lastname);
        toCreate.setAccountType(accountType);
        toCreate.setRegisteredSince(registeredSince);

        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(toCreate);
        manager.getTransaction().commit();

        // when
        boolean result = repository.create(toCreate);

        // then
        assertFalse(result);
    }
}
