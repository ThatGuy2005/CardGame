package kartya.jatekos;

import kartya.jatek.Konzol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static kartya.TesztLapok.lap;
import static kartya.TesztLapok.lapok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmberiJatekosTest {

    @Test
    @DisplayName("Érvényes bevitel: szóköz vagy vessző elválasztó, üres sor = nincs csere")
    void ervenyesBevitel() {
        assertEquals(Set.of(1, 3, 5), EmberiJatekos.sorszamokFeldolgozasa("1 3 5", 5));
        assertEquals(Set.of(2, 4), EmberiJatekos.sorszamokFeldolgozasa(" 4,2 ", 5));
        assertEquals(Set.of(), EmberiJatekos.sorszamokFeldolgozasa("", 5));
    }

    @ParameterizedTest(name = "„{0}”")
    @ValueSource(strings = {"x", "0", "6", "1 1", "1 2 3 4", "1;2"})
    @DisplayName("Hibás bevitel kivételt dob")
    void hibasBevitel(String sor) {
        assertThrows(IllegalArgumentException.class, () -> EmberiJatekos.sorszamokFeldolgozasa(sor, 5));
    }

    @Test
    @DisplayName("Hibás bevitel után újra kérdez, majd a helyes válasz lapjait adja vissza")
    void hibasBevitelUtanUjraKerdez() {
        ByteArrayOutputStream kimenet = new ByteArrayOutputStream();
        Konzol konzol = new Konzol(
                new BufferedReader(new StringReader("9\nabc\n2 5\n")),
                new PrintStream(kimenet, true, StandardCharsets.UTF_8),
                false);
        EmberiJatekos jatekos = new EmberiJatekos("Teszt", konzol);
        lapok("Ap Kk 9o 5t 2t").forEach(jatekos.kez()::hozzaad);

        assertEquals(Set.of(lap("Kk"), lap("2t")), jatekos.eldobandok());
        String szoveg = kimenet.toString(StandardCharsets.UTF_8);
        assertEquals(2, szoveg.split("Hibás bemenet", -1).length - 1);
    }

    @Test
    @DisplayName("Bemenet vége (EOF) esetén nem cserél")
    void bemenetVege() {
        Konzol konzol = new Konzol(new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8), false);
        EmberiJatekos jatekos = new EmberiJatekos("Teszt", konzol);
        lapok("Ap Kk 9o 5t 2t").forEach(jatekos.kez()::hozzaad);

        assertTrue(jatekos.eldobandok().isEmpty());
    }
}
