package at.campus02.dbp2.mappings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerRepositoryCrudSpec {
    //#region test data
    private final String firstname = "Firstname";
    private final String lastname = "Lastname";
    private final AccountType accountType = AccountType.BASIC;
    private final LocalDate registeredSince = LocalDate.of(2021, 10, 1);

    private Customer initDefaultCustomer() {
        Customer customer = new Customer();
        customer.setFirstname(firstname);
        customer.setLastname(lastname);
        customer.setAccountType(accountType);
        customer.setRegisteredSince(registeredSince);
        return customer;
    }
    //#endregion

    //#region setup / tear down
    private EntityManagerFactory factory;
    private CustomerRepository repository;
    private EntityManager manager;

    @BeforeEach
    public void beforeEach() {
        factory = Persistence.createEntityManagerFactory("persistenceUnitName");
        repository = new CustomerRepositoryJpa(factory);
        manager = factory.createEntityManager();
    }

    @AfterEach
    public void afterEach() {
        if (manager.isOpen())
            manager.close();
        if (factory.isOpen())
            factory.close();
    }
    //#endregion

    //#region CRUD: create
    @Test
    public void createNullAsCustomerReturnsFalse() {

        boolean result = repository.create(null);

        assertFalse(result);
    }

    @Test
    public void createPersistsCustomerInDatabaseAndReturnsTrue() {
        // given
        Customer toCreate = initDefaultCustomer();
        // when
        boolean result = repository.create(toCreate);

        // then
        assertTrue(result);
        // Kontrolle auch in der DB
        Customer fromDB = manager.find(Customer.class, toCreate.getId());
        assertEquals(firstname, fromDB.getFirstname());
        assertEquals(lastname, fromDB.getLastname());
        assertEquals(accountType, fromDB.getAccountType());
        assertEquals(registeredSince, fromDB.getRegisteredSince());
    }

    @Test
    public void createExistingCustomerReturnsFalse() {
        // given
        Customer toCreate = initDefaultCustomer();

        manager.getTransaction().begin();
        manager.persist(toCreate);
        manager.getTransaction().commit();

        // when
        boolean result = repository.create(toCreate);

        // then
        assertFalse(result);
    }

    @Test
    public void createCustomerWithNullAsAccountTypeThrowsException() {
        // given
        Customer notValid = initDefaultCustomer();
        notValid.setAccountType(null);

        // when / then
        assertThrows(RuntimeException.class,() -> repository.create(notValid));
    }
    //#endregion

    //#region CRUD: read
    @Test
    public void readFindsCustomerInDatabase() {
        // given
        Customer existing = initDefaultCustomer();

        manager.getTransaction().begin();
        manager.persist(existing);
        manager.getTransaction().commit();

        // when
        Customer fromRepository = repository.read(existing.getId());

        // then
        assertEquals(existing.getId(), fromRepository.getId());
        assertEquals(firstname, fromRepository.getFirstname());
        assertEquals(lastname, fromRepository.getLastname());
        assertEquals(accountType, fromRepository.getAccountType());
        assertEquals(registeredSince, fromRepository.getRegisteredSince());
    }

    @Test
    public void readWithNotExistingIdReturnsNull() {
        // when
        Customer fromRepository = repository.read(-1);

        // then
        assertNull(fromRepository);
    }

    @Test
    public void readWithNullAsIdReturnsNull() {
        // when
        Customer fromRepository = repository.read(null);

        // then
        assertNull(fromRepository);
    }
    //#endregion

    //#region CRUD: update
    @Test
    public void updateChangesAttributesInDatabase() {
        // given
        Customer existing = initDefaultCustomer();

        manager.getTransaction().begin();
        manager.persist(existing);
        manager.getTransaction().commit();

        String changedFirstname = "ChangedFirstName";
        String changedLastname = "ChangedLastName";
        AccountType changedAccountType = AccountType.PREMIUM;
        LocalDate changedRegisteredSince = LocalDate.of(2021,10,14);

        // when
        existing.setFirstname(changedFirstname);
        existing.setLastname(changedLastname);
        existing.setAccountType(changedAccountType);
        existing.setRegisteredSince(changedRegisteredSince);
        Customer updated = repository.update(existing);

        // then
        assertEquals(existing.getId(), updated.getId());
        assertEquals(changedFirstname, updated.getFirstname());
        assertEquals(changedLastname, updated.getLastname());
        assertEquals(changedAccountType, updated.getAccountType());
        assertEquals(changedRegisteredSince, updated.getRegisteredSince());

        // also check values from database
        // clear cache to ensure reading from DB again
        manager.clear();
        Customer fromDB = manager.find(Customer.class, updated.getId());
        assertEquals(updated.getId(), fromDB.getId());
        assertEquals(changedFirstname, fromDB.getFirstname());
        assertEquals(changedLastname, fromDB.getLastname());
        assertEquals(changedAccountType, fromDB.getAccountType());
        assertEquals(changedRegisteredSince, fromDB.getRegisteredSince());
    }

    @Test
    public void updateNotExistingCustomerThrowsIllegalArgumentException() {
        // given
        Customer notExisting = initDefaultCustomer();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> repository.update(notExisting));
    }

    @Test
    public void updateWithNullAsCustomerReturnsNull() {
        // when
        Customer updated = repository.update(null);

        // then
        assertNull(updated);
    }
    //#endregion

    //#region CRUD: delete
    @Test
    public void deleteRemovesCustomerFromDatabaseAndReturnsTrue() {
        // given
        Customer existing = initDefaultCustomer();

        manager.getTransaction().begin();
        manager.persist(existing);
        manager.getTransaction().commit();

        // when
        boolean result = repository.delete(existing);

        // then
        assertTrue(result);

        // also then
        manager.clear();
        Customer hopefullyDeleted = manager.find(Customer.class, existing.getId());
        assertNull(hopefullyDeleted);
    }

    @Test
    public void deleteNonExistingCostumerThrowsIllegalArgumentException() {
        // given
        Customer notExisting = initDefaultCustomer();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> repository.delete(notExisting));
    }

    @Test
    public void deleteNullAsCustomerReturnsFalse() {
        // when
        boolean result = repository.delete(null);

        // then
        assertFalse(result);
    }
    //#endregion
}
