import at.campus02.dbp2.relations.Animal;
import at.campus02.dbp2.relations.Species;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.hamcrest.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OneToManyTests {
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
    public void persistSpeciesWithCascadeStoresAnimalsInDatabase() {
        // given
        Animal bear = new Animal("Trinket");
        Animal cat = new Animal("Frumpkin");
        Animal monkey = new Animal("Little Mister");
        Species mammals = new Species("Mammals");

        // Referenzen für FK in der DB
        bear.setSpecies(mammals);
        cat.setSpecies(mammals);
        monkey.setSpecies(mammals);
        // Referenzen für CASCADE
        mammals.addAnimals(bear);
        mammals.addAnimals(cat);
        mammals.addAnimals(monkey);
        // when
        manager.getTransaction().begin();
        manager.persist(mammals);
        manager.getTransaction().commit();

        manager.clear();

        // then
        Species mammalsFromDb = manager.find(Species.class, mammals.getId());
        assertThat(mammalsFromDb.getAnimals().size(), is(3));
        assertThat(mammalsFromDb.getAnimals(), containsInAnyOrder(bear, cat, monkey));
    }

    @Test
    @Disabled("only works without orphanRemoval - enable after setting orphanRemoval to false")
    public void updateExampleWithCorrectingReferences() {
        Animal owl = new Animal("Professor Thaddeus");
        Animal weasel = new Animal("Sprinkle");
        Species bird = new Species("Bird");

        // Referenzen für DB
        owl.setSpecies(bird);
        // FEHLER -> den wollen wir dann korrigieren
        weasel.setSpecies(bird);

        // Referenzen für CASCADE
        bird.addAnimals(owl);
        bird.addAnimals(weasel);

        // Speichern
        manager.getTransaction().begin();
        manager.persist(bird);
        manager.getTransaction().commit();

        manager.clear();

        // when
        // Korrekturversuch, zum Scheitern verurteilt
        manager.getTransaction().begin();
        bird.getAnimals().remove(weasel);
        manager.merge(bird);
        manager.getTransaction().commit();
        manager.clear();

        // then
        // Sprinkle existiert noch in DB
        Animal sprinkleFromDb = manager.find(Animal.class, weasel.getId());
        assertThat(sprinkleFromDb, is(notNullValue()));

        // Sprinkle ist immer noch ein Vogel - wir haben im Speicher die Liste von "bird" geändert,
        // aber species von Sprinkle zeigt nach wie vor auf bird, auch in der DB.
        assertThat(sprinkleFromDb.getSpecies(), is(bird));

        // auch wenn wir die Liste mittels "refresh" neu einlesen, wird die Referenz von Sprinkle auf Bird (DB)
        // neu eingelesen und Sprinkle ist wieder in der Liste drin.
        Species mergedBird = manager.merge(bird);
        manager.refresh(mergedBird);
        assertThat(mergedBird.getAnimals().size(), is(2));

        // when
        // Korrekturversuch, diesmal richtig
        manager.getTransaction().begin();
        bird.getAnimals().remove(weasel);
        weasel.setSpecies(null);
        manager.merge(bird);
        manager.getTransaction().commit();
        manager.clear();

        // then
        // Sprinkle existiert noch in DB
        sprinkleFromDb = manager.find(Animal.class, weasel.getId());
        assertThat(sprinkleFromDb, is(notNullValue()));

        // Sprinkle ist kein Vogel mehr
        assertThat(sprinkleFromDb.getSpecies(), is(nullValue()));

        // auch wenn wir die Liste mittels "refresh" neu einlesen, ist Sprinkle nicht mehr enthalten.
        mergedBird = manager.merge(bird);
        manager.refresh(mergedBird);
        assertThat(mergedBird.getAnimals().size(), is(1));
    }

    @Test
    public void orphanRemovalDeletesOrphansFromDatabase() {
        // given
        Animal owl = new Animal("Professor Thaddeus");
        Animal weasel = new Animal("Sprinkle");
        Species bird = new Species("Bird");

        // Referenzen für DB
        owl.setSpecies(bird);
        // FEHLER -> den wollen wir dann korrigieren
        weasel.setSpecies(bird);

        // Referenzen für CASCADE
        bird.addAnimals(owl);
        bird.addAnimals(weasel);

        // Speichern
        manager.getTransaction().begin();
        manager.persist(bird);
        manager.getTransaction().commit();

        manager.clear();

        // when
        manager.getTransaction().begin();
        bird.getAnimals().remove(weasel);
        manager.merge(bird);
        manager.getTransaction().commit();

        manager.clear();

        // then
        Animal sprinkleFromDb = manager.find(Animal.class, weasel.getId());
        // bei Verwendung von orphanRemoval wird Sprinkle aus der DB gelöscht.
        assertThat(sprinkleFromDb, is(nullValue()));

        Species refreshedBird = manager.merge(bird);
        manager.refresh(refreshedBird);

        assertThat(refreshedBird.getAnimals().size(), is(1));
    }
}
