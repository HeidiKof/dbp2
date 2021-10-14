package at.campus02.dbp2.mappings;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Customer {

    @Id @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate registeredSince;
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
}
