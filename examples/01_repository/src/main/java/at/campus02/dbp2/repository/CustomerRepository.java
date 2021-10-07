package at.campus02.dbp2.repository;

public interface CustomerRepository {
    void create(Customer customer);
    Customer read(String email);
    void update(Customer customer);
    void delete(Customer customer);
}
