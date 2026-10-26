package kartya.strategia;

import kartya.model.Kartya;
import kartya.model.Kez;
import kartya.model.Pakli;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static kartya.TesztLapok.kez;
import static kartya.TesztLapok.lapok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategiaTest {

    static Stream<Strategia> strategiak() {
        return Stream.of(new OvatosStrategia(), new KockazatosStrategia());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strategiak")
    @DisplayName("Sok véletlen kézre is érvényes eldobási halmazt ad (≤ 3 lap, a kéz része)")
    void ervenyesEldobasiHalmaz(Strategia strategia) {
        Random random = new Random(2024);
        for (int i = 0; i < 1000; i++) {
            Pakli pakli = Pakli.francia52();
            pakli.kever(random);
            Kez kez = new Kez();
            for (int j = 0; j < Kez.MAX_LAPSZAM; j++) {
                kez.hozzaad(pakli.huz());
            }

            Set<Kartya> eldobandok = strategia.eldobandok(kez);
            assertTrue(eldobandok.size() <= Strategia.MAX_ELDOBAS, () -> kez + " → " + eldobandok);
            assertTrue(kez.lapok().containsAll(eldobandok), () -> kez + " → " + eldobandok);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("strategiak")
    @DisplayName("Kész sort vagy jobbat egyik stratégia sem bont meg")
    void keszKeztNemBont(Strategia strategia) {
        assertEquals(Set.of(), strategia.eldobandok(kez("9t 10k Bo Dp Kt")));
        assertEquals(Set.of(), strategia.eldobandok(kez("9t 9k 9o Kp Kt")));
    }

    @Test
    @DisplayName("Óvatos: a párt megtartja, a három kísérőt cseréli")
    void ovatosParMegtart() {
        assertEquals(new HashSet<>(lapok("3t 7t Kt")),
                new OvatosStrategia().eldobandok(kez("9t 9k 3t 7t Kt")));
    }

    @Test
    @DisplayName("Óvatos: semmi esetén a két legmagasabb lapot tartja meg")
    void ovatosKetLegmagasabb() {
        assertEquals(new HashSet<>(lapok("2t 5k 9o")),
                new OvatosStrategia().eldobandok(kez("2t 5k 9o Bo Kp")));
    }

    @Test
    @DisplayName("Kockázatos: flushhúzásért feláldozza a párt")
    void kockazatosFlushHuzas() {
        assertEquals(Set.copyOf(lapok("9k")),
                new KockazatosStrategia().eldobandok(kez("9t 9k 3t 7t Kt")));
    }

    @Test
    @DisplayName("Kockázatos: nyitott és kis-ászos sorhúzást is felismer")
    void kockazatosSorHuzas() {
        assertEquals(Set.copyOf(lapok("2t")),
                new KockazatosStrategia().eldobandok(kez("2t 6k 7o 8p 9t")));
        assertEquals(Set.copyOf(lapok("Kp")),
                new KockazatosStrategia().eldobandok(kez("Ao 2t 3k 4t Kp")));
    }
}
