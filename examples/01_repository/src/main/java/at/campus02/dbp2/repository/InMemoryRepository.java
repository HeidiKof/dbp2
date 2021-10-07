package at.campus02.dbp2.repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository implements CustomerRepository {

    private Map<String,Customer> storage = new HashMap<>();

    @Override
    public void create(Customer customer) {
        storage.put(customer.getEmail(), customer);
    }

    @Override
    public Customer read(String email) {
        return storage.get(email);
    }

    @Override
    public void update(Customer customer) {
        storage.replace(customer.getEmail(), customer);
    }

    @Override
    public void delete(Customer customer) {
        storage.remove(customer.getEmail());
    }
}
