package at.campus02.dbp2.mappings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerRepositoryQuerySpec {

    //#region setup / tear down
    private EntityManagerFactory factory;
    private CustomerRepository repository;
    private EntityManager manager;

    private Customer customer1;
    private Customer customer2;
    private Customer customer3;
    private Customer customer4;
    private Customer customer5;
    private Customer customer6;
    private Customer customer7;

    private void setupCommonTestData() {
        customer1 = new Customer();
        customer1.setFirstname("Albert");
        customer1.setLastname("Aarhus");
        customer1.setAccountType(AccountType.BASIC);
        customer1.setRegisteredSince(LocalDate.of(2021, 1, 1));

        customer2 = new Customer();
        customer2.setFirstname("Bernadette");
        customer2.setLastname("Brandtner");
        customer2.setAccountType(AccountType.PREMIUM);
        customer2.setRegisteredSince(LocalDate.of(2021, 2, 2));

        customer3 = new Customer();
        customer3.setFirstname("Charlie");
        customer3.setLastname("Chandler");
        customer3.setAccountType(AccountType.PREMIUM);
        customer3.setRegisteredSince(LocalDate.of(2021, 3, 3));

        customer4 = new Customer();
        customer4.setFirstname("Dollie");
        customer4.setLastname("Dornacher");
        customer4.setAccountType(AccountType.BASIC);
        customer4.setRegisteredSince(LocalDate.of(2021, 4, 4));

        customer5 = new Customer();
        customer5.setFirstname("Emil");
        customer5.setLastname("Eberhard");
        customer5.setAccountType(AccountType.PREMIUM);
        customer5.setRegisteredSince(LocalDate.of(2021, 5, 5));

        customer6 = new Customer();
        customer6.setFirstname("Charlotte");
        customer6.setLastname("Eberstolz");
        customer6.setAccountType(AccountType.BASIC);
        customer6.setRegisteredSince(LocalDate.of(2021, 6, 6));

        customer7 = new Customer();
        customer7.setFirstname("Bernhard");
        customer7.setLastname("Hornbacher");
        customer7.setAccountType(AccountType.BASIC);
        customer7.setRegisteredSince(LocalDate.of(2021, 7, 7));

        manager.getTransaction().begin();
        manager.persist(customer5);
        manager.persist(customer4);
        manager.persist(customer1);
        manager.persist(customer2);
        manager.persist(customer3);
        manager.persist(customer7);
        manager.persist(customer6);
        manager.getTransaction().commit();
    }

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

    @Test
    public void getAllCustomersReturnsAllCustomersFromDbSortedByRegistrationDate() {
        // given
        setupCommonTestData();

        // when
        List<Customer> sortedCustomers = repository.getAllCustomers();

        // then
        assertIterableEquals(
                Arrays.asList(customer1, customer2, customer3, customer4, customer5, customer6, customer7),
                sortedCustomers
        );
    }

    @Test
    public void getAllCustomersOnEmptyDatabaseReturnsEmptyList() {
        // when
        List<Customer> sortedCustomers = repository.getAllCustomers();

        // then
        assertThat(sortedCustomers, is((empty())));
        assertTrue(sortedCustomers.isEmpty());
    }

    @Test
    public void findByAccountTypeReturnsMatchingCustomers() {
        // given
        setupCommonTestData();

        // when
        List<Customer> basic = repository.findByAccountType(AccountType.BASIC);
        List<Customer> premium = repository.findByAccountType(AccountType.PREMIUM);

        // then
        assertThat(basic, containsInAnyOrder(customer1,customer4,customer6,customer7));
        assertThat(premium, containsInAnyOrder(customer2,customer3,customer5));

//        List<Customer> expectedBasic = Arrays.asList(customer1, customer4, customer6, customer7);
//        List<Customer> expectedPremium = Arrays.asList(customer2, customer3, customer5);
//
//        assertTrue(
//                expectedBasic.size() == basic.size()
//                && expectedBasic.containsAll(basic)
//                && basic.containsAll(expectedBasic)
//        );
//        assertTrue(
//                expectedPremium.size() == premium.size()
//                        && expectedPremium.containsAll(premium)
//                        && premium.containsAll(expectedPremium)
//        );
    }

    @Test
    public void findByAccountTypeNullReturnsEmptyList() {
        // given
        setupCommonTestData();

        // when
        List<Customer> result = repository.findByAccountType(null);

        // then
        assertThat(result, is(empty()));
    }
    //#endregion

    //#region Query: findByLastName
    @Test
    public void findByLastNameReturnsMatchingCustomers() {
        // given
        setupCommonTestData();

        // when
        List<Customer> matching = repository.findByLastname("orn");

        // then
        assertThat(matching, contains(customer4, customer7));
    }

    @Test
    public void findByLastNameReturnsCaseInsensitivelyMatchingCustomers() {
        // given
        setupCommonTestData();

        // when
        List<Customer> matching = repository.findByLastname("eBEr");

        // then
        assertThat(matching, contains(customer5, customer6));
    }

    @Test
    public void findByLastNameWithNullOrEmptyStringReturnsEmptyList() {
        // given
        setupCommonTestData();

        // when
        List<Customer> matching = repository.findByLastname("");

        // then
        assertThat(matching, is(empty()));

        // and when
        matching = repository.findByLastname(null);

        // then
        assertThat(matching, is(empty()));
    }
    //#endregion

    //#region Query: findAllRegisteredAfter
    @Test
    public void findAllRegisteredAfterReturnsMatchingCustomers() {
        // given
        setupCommonTestData();

        // when
        List<Customer> matching = repository.findAllRegisteredAfter(LocalDate.of(2021, 4, 4));

        //then
        assertThat(matching, containsInAnyOrder(customer5, customer6, customer7));

        // and when
        matching = repository.findAllRegisteredAfter(LocalDate.of(2021, 4, 3));

        //then
        assertThat(matching, containsInAnyOrder(customer4, customer5, customer6, customer7));
    }
    //#endregion
}
