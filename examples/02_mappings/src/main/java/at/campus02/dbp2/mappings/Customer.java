package at.campus02.dbp2.mappings;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@NamedQuery(name = "Customer.findByLastNamePart",
            query = "select c from Customer c " +
                    "where lower(c.lastname) like lower(:lastnamePart) " +
                    "order by c.lastname")
@Entity
public class Customer {

    @Id @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate registeredSince;
    @Column(nullable = false)
    private AccountType accountType;

    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getRegisteredSince() {
        return registeredSince;
    }

    public void setRegisteredSince(LocalDate registeredSince) {
        this.registeredSince = registeredSince;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(firstname, customer.firstname) &&
                Objects.equals(lastname, customer.lastname) &&
                Objects.equals(registeredSince, customer.registeredSince) &&
                accountType == customer.accountType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, registeredSince, accountType);
    }
}
