# Beispiel 2: Mappings, Queries und Tests

In diesem Beispiel geht es darum, verschiedene DatenTypen automatisch zu mappen
und via JPQL unterschiedliche Queries zu implementieren. 

## Aufgabenstellung

Es soll Funktionalität zum Verwalten und Suchen von Kunden in einer einfachen Kundendatenbank 
zur Verfügung gestellt werden. 

Kunden sind dabei wie folgt definiert:

```java
public class Customer {
    public Integer getId();

    public String getFirstname();
    public void setFirstname(String firstname);

    public String getLastname();
    public void setLastname(String lastname);

    public LocalDate getRegisteredSince();
    public void setRegisteredSince(LocalDate registeredSince);
    
    public AccountType getAccountType();
    public void setAccountType(AccountType accountType);
}
``` 

* `id`: technische ID, soll beim Erzeugen automatisch generiert werden
* `registeredSince`: das Datum der Registrierung des Kunden als `LocalDate`
* `accountType`: eine `enum`-Klasse (``public enum AccountType { BASIC, PREMIUM }``)

Der Zugriff auf die Funktionalität erfolgt über ein ``CustomerRepository``, das wie folgt definiert ist:

```java
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
```

* Die Implementierung erfolgt über die Klasse ``CustomerRepositoryJpa``.
* Die Überprüfung der Funktionalität erfolgt über Unit-Tests. Dabei sollte ein Testfile den CRUD-Teil überprüfen, 
  ein zweites Testfile den Query-Teil.
* Zusätzliche Anforderungen:
  * Zumindest eine Query sollte als ``NamedQuery`` implementiert werden. 
  * Die von ``getAllCustomers`` zurückgegebene Liste soll aufsteigend nach ``registeredSince`` sortiert sein.
  * ``findByLastname``:
    * die zurückgegebene Liste soll aufsteigend nach ``lastname`` sortiert sein. 
    * Es sollen alle Kunden gefunden werden, die den Parameter ``lastnamePart`` als Teil des Nachnamens enthalten.
    * Die Suche soll case-insensitive funktionieren.