# Beispiel 1

Als Erinnerung/Einführung soll eine einfache Applikation 
erstellt werden, die in ihrer ``main`` Methode einen Kunden (``Customer``)
anlegen, auslesen, ändern und löschen kann ([CRUD](https://de.wikipedia.org/wiki/CRUD)).

Dabei werden wir drei verschiedene Varianten umsetzen: 
* In-Memory-Speicherung 
* Datenbankanbindung mittels JDBC
* Datenbankanbindung mittels Object-Relational-Mapping (bei uns: JPA)


Für die gemeinsame Funktionalität der drei Implementierungen wird in der Applikation ein Interface
verwendet (``CustomerRepository``). Dadurch kann die jeweilige Implementierung einfach ausgetauscht werden.

```java
public interface CustomerRepository {
    void create(Customer customer);
    Customer read(String email);
    void update(Customer customer);
    void delete(Customer customer);

}
```

Die Klasse ``Customer`` soll dabei folgende Funktionalität zur Verfügung stellen:

```java
public class Customer {
    public String getLastname();
    public void setLastname(String lastname);

    public String getFirstname();
    public void setFirstname(String firstname);

    public String getEmail();
    public void setEmail(String email);
}
```

In der Applikation selbst (``Application``) wird dabei eine spezifische Implementierung von ``CustomerRepository``
verwendet, um einen ``Customer`` zu erzeugen, auszulesen, verändern und zu löschen.


## Teil 1: In-Memory

Die In-Memory-Variante verwendet als "Datenbank" lediglich eine ``java`` Map (Key: email-Adresse, Value: Customer).


## Teil 2: JDBC

Als Datenbank verwenden wir "Derby" (mit dem "embedded" JDBC Treiber), die Datenbank-Files sollen
dabei im Projekt im Ordner "database" abgelegt werden. Der Treiber ist bereits in den Abhängigkeiten des Projekts
definiert und verfügbar.

Der Zugriff erfolgt mittels ``PreparedStatement``s über eine ``Connection``, die im Konstruktor
der Implementierung erzeugt werden soll. 

Außerdem soll die Tabelle ``CUSTOMER`` erzeugt werden, sofern sie noch nicht existiert. 

## Teil 3: JPA

Als Datenbank verwenden wir "Derby" (mit dem "embedded" JDBC Treiber), die Datenbank-Files sollen
dabei im Projekt im Ordner "database" abgelegt werden. 

Die verwendeten Libraries sind bereits in den Abhängigkeiten definiert und verfügbar:

* JPA
* JPA Implementierung (EclipseLink)
* Derby 




