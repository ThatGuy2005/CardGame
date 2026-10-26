package kartya.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PakliTest {

    @Test
    @DisplayName("A francia pakli 52 különböző lapból áll")
    void francia52() {
        Pakli pakli = Pakli.francia52();
        assertEquals(52, pakli.meret());
        assertEquals(52, new HashSet<>(pakli.lapok()).size());
    }

    @Test
    @DisplayName("A 32 lapos pakli 32 különböző lapból áll, 7-estől ászig")
    void magyar32() {
        Pakli pakli = Pakli.magyar32();
        assertEquals(32, pakli.meret());

        Set<Kartya> lapok = new HashSet<>(pakli.lapok());
        assertEquals(32, lapok.size());
        assertTrue(lapok.stream().allMatch(k -> k.ertek().compareTo(Ertek.HET) >= 0));
        assertEquals(Ertek.HET, pakli.legkisebbErtek());
    }

    @Test
    @DisplayName("Azonos seed → azonos keverés; eltérő seed → eltérő keverés")
    void seedeltKeveres() {
        Pakli a = Pakli.francia52();
        Pakli b = Pakli.francia52();
        Pakli c = Pakli.francia52();
        a.kever(new Random(42));
        b.kever(new Random(42));
        c.kever(new Random(43));

        assertEquals(a.lapok(), b.lapok());
        assertNotEquals(a.lapok(), c.lapok());
        assertNotEquals(Pakli.francia52().lapok(), a.lapok(), "A keverés megváltoztatja a sorrendet");
    }

    @Test
    @DisplayName("A húzás a tetejéről vesz, és üres paklinál kivételt dob")
    void huzasAmigKiurul() {
        Pakli pakli = Pakli.magyar32();
        Kartya teteje = pakli.lapok().getFirst();

        assertEquals(teteje, pakli.huz());
        assertEquals(31, pakli.meret());

        while (!pakli.ures()) {
            pakli.huz();
        }
        assertTrue(pakli.ures());
        assertThrows(NoSuchElementException.class, pakli::huz);
        assertFalse(Pakli.francia52().ures());
    }
}
