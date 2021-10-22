import at.campus02.dbp2.relations.Animal;
import at.campus02.dbp2.relations.Student;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class OneToOneTests {
    private EntityManagerFactory factory;
    private EntityManager manager;

    @BeforeEach
    public void setup() {
        factory = Persistence
                .createEntityManagerFactory("persistenceUnitName");
        manager = factory.createEntityManager();
    }

    @AfterEach
    public void teardown() {
        if (manager.isOpen()) {
            manager.close();
        }
        if (factory.isOpen()) {
            factory.close();
        }
    }

    @Test
    public void persistAnimalAndStudentStoresRelationInDatabase() {
        // given
        Student student = new Student("Fearne");
        Animal animal = new Animal("Mister");

        // im Speicher selber um die Referenz kümmern
        student.setAnimal(animal);
        animal.setStudent(student);

        // when
        manager.getTransaction().begin();
        manager.persist(student);
        manager.persist(animal);
        manager.getTransaction().commit();

        // then
        Animal mister = manager.find(Animal.class, animal.getId());
        assertThat(mister.getStudent(), CoreMatchers.is(student));

        Student ownerFromDb = manager.find(Student.class, student.getId());
        assertThat(ownerFromDb.getAnimal(), CoreMatchers.is(animal));
    }

    @Test
    public void persistStudentWithCascadeAlsoPersistsAnimal() {
        // given
        Student caleb = new Student("Caleb");
        Animal frumpkin = new Animal("Frumpkin");

        // im Speicher selber um die Referenz kümmern
        // 1) Owner setzen, um in der DB die Relation zu schließen
        caleb.setAnimal(frumpkin);
        // 2) Pet setzen, damit CASCADE funktioniert
        frumpkin.setStudent(caleb);

        // when
        manager.getTransaction().begin();
        manager.persist(caleb);
        // frumpkin soll durch cascade mit caleb mitgespeichert werden
        manager.getTransaction().commit();

        // then
        Animal catFromDb = manager.find(Animal.class, frumpkin.getId());
        assertThat(catFromDb.getStudent(), CoreMatchers.is(caleb));

        Student wizardFromDb = manager.find(Student.class, caleb.getId());
        assertThat(wizardFromDb.getAnimal(), CoreMatchers.is(frumpkin));
    }

    @Test
    public void refreshClosesReferencesNotHandledInMemory() {
        // given
        Student vex = new Student("Vex'ahlia");
        Animal trinket = new Animal("Trinket");

        // im Speicher selber um die Referenz kümmern
        // 1) Owner setzen, um in der DB die Relation zu schließen
        trinket.setStudent(vex);
        // 2) Pet setzen, damit CASCADE funktioniert
        // trinket.setStudent(vex);

        // when
        manager.getTransaction().begin();
        manager.persist(trinket);
        // nachdem auf "vex" kein Pet gesetzt ist, reicht es nicht, "vex" allein zu persistieren
        // (Cascade kann nicht greifen). D.h. wir müssen beide Entities persistieren
        // (Reihenfolge innerhalb der Transaktion ist egal).
        manager.persist(vex);
        manager.getTransaction().commit();

        manager.clear();

        // then
        // 1) Referenz von Animal auf Student ist gesetzt
        Animal bearFromDb = manager.find(Animal.class, trinket.getId());
        assertThat(bearFromDb.getStudent(), Matchers.is(vex));

        // 2) ohne refresh wird die Referenz von "vex" auf "trinket"
        // nicht geschlossen
        Student rangerFromDb = manager.find(Student.class, vex.getId());
        assertThat(rangerFromDb.getAnimal(), is(nullValue()));

        // 3) "refresh erzwingt das Neu-Einlesen aus der Datenbank, auch mit Relationen.
        manager.refresh(rangerFromDb);
        assertThat(rangerFromDb.getAnimal(), Matchers.is(trinket));
    }
}
