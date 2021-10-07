package at.campus02.dbp2.repository;

public class Application {

    public static void log(String msg) {
        System.out.println("Application:  --> " + msg);
    }

    public static void main(String[] args) {
        log("application started");

        CustomerRepository repository = new InMemoryRepository();

        Customer customer = new Customer();
        customer.setEmail("customer1@customers.com");
        customer.setFirstname("Carlo");
        customer.setLastname("Customer");

        repository.create(customer);
        log("Customer created: " + customer);

        Customer fromRepository = repository.read(customer.getEmail());
        log("Customer read: " + fromRepository);

        fromRepository.setFirstname("Conrad");
        repository.update(fromRepository);
        Customer updated = repository.read(fromRepository.getEmail());
        log("Customer updated " + updated);

        repository.delete(updated);
        Customer deleted = repository.read(updated.getEmail());
        log("Customer deleted: " + deleted);
    }
}
