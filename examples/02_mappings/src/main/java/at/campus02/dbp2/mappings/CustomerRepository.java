package at.campus02.dbp2.mappings;

import java.time.LocalDate;
import java.util.List;

public interface CustomerRepository {

    boolean create(Customer customer);
    Customer read(Integer id);
    Customer update(Customer customer);
    boolean delete(Customer customer);

    List<Customer> getAllCustomers();
    List<Customer> findByLastname(String lastnamePart);
    List<Customer> findByAccountType(AccountType type);
    List<Customer> findAllRegisteredAfter(LocalDate date);

}
