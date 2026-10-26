package kartya.jatek;

import kartya.ertekeles.KezErtekelo;
import kartya.jatekos.EmberiJatekos;
import kartya.jatekos.GepiJatekos;
import kartya.jatekos.Jatekos;
import kartya.model.Pakli;
import kartya.strategia.KockazatosStrategia;
import kartya.strategia.OvatosStrategia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JatekTest {

    /** Lejátszik egy játszmát a megadott bemenettel, és visszaadja a teljes kimenetet. */
    private static String jatszma(long seed, String bemenet) {
        ByteArrayOutputStream kimenet = new ByteArrayOutputStream();
        Konzol konzol = new Konzol(new BufferedReader(new StringReader(bemenet)),
                new PrintStream(kimenet, true, StandardCharsets.UTF_8), true);
        KezErtekelo ertekelo = new KezErtekelo();
        List<Jatekos> jatekosok = List.of(
                new EmberiJatekos("Te", konzol),
                new GepiJatekos("Olga", new OvatosStrategia(ertekelo)),
                new GepiJatekos("Karcsi", new KockazatosStrategia(ertekelo)));
        new Jatek(jatekosok, Pakli::francia52, new Random(seed), ertekelo, konzol).futtat();
        return kimenet.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Azonos seed és bemenet mellett a játszma pontosan megismételhető")
    void seedeltJatszmaVisszajatszhato() {
        String bemenet = "1 2\ni\n\ni\n3\nn\n";
        String elso = jatszma(7, bemenet);

        assertEquals(elso, jatszma(7, bemenet));
        assertTrue(elso.contains("===== 3. kör ====="));
        assertTrue(elso.contains("Köszönöm a játékot!"));
    }

    @Test
    @DisplayName("Túl sok játékos a 32 lapos paklihoz: kivétel")
    void tulSokJatekos() {
        Konzol konzol = new Konzol(new BufferedReader(new StringReader("")),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8), false);
        List<Jatekos> otJatekos = List.of(
                new GepiJatekos("A", new OvatosStrategia()), new GepiJatekos("B", new OvatosStrategia()),
                new GepiJatekos("C", new OvatosStrategia()), new GepiJatekos("D", new OvatosStrategia()),
                new GepiJatekos("E", new OvatosStrategia()));

        assertThrows(IllegalArgumentException.class,
                () -> new Jatek(otJatekos, Pakli::magyar32, new Random(1), new KezErtekelo(), konzol));
    }
}
