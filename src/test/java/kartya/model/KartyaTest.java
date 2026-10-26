package kartya.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static kartya.TesztLapok.lap;
import static kartya.TesztLapok.lapok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KartyaTest {

    @Test
    @DisplayName("Természetes rendezés: először érték, majd szín")
    void termeszetesRendezes() {
        List<Kartya> lista = new ArrayList<>(lapok("Ap 2o Kt 2t Ak 10o"));
        lista.sort(null);
        assertEquals(lapok("2t 2o 10o Kt Ak Ap"), lista);
    }

    @Test
    @DisplayName("Az egyenlő lapok egybeesnek HashSetben")
    void egyenloLapokHashSetben() {
        Set<Kartya> halmaz = new HashSet<>();
        halmaz.add(new Kartya(Szin.KOR, Ertek.DAMA));
        halmaz.add(new Kartya(Szin.KOR, Ertek.DAMA));
        halmaz.add(new Kartya(Szin.PIKK, Ertek.DAMA));

        assertEquals(2, halmaz.size());
        assertTrue(halmaz.contains(lap("Do")));
        assertEquals(new Kartya(Szin.KOR, Ertek.DAMA).hashCode(), lap("Do").hashCode());
    }

    @Test
    @DisplayName("A lap HashMap kulcsként működik (új, egyenlő példánnyal is visszakereshető)")
    void hashMapKulcs() {
        Map<Kartya, String> tulajdonos = new HashMap<>();
        tulajdonos.put(lap("At"), "Olga");
        tulajdonos.put(lap("At"), "Karcsi");

        assertEquals(1, tulajdonos.size());
        assertEquals("Karcsi", tulajdonos.get(new Kartya(Szin.TREFF, Ertek.ASZ)));
    }

    @Test
    @DisplayName("compareTo konzisztens az equals-szal: TreeSet és HashSet ugyanannyi elemet tart")
    void compareToKonzisztensEqualsszal() {
        List<Kartya> lista = lapok("Ap Ak Ap 7o 7o 7t");
        assertEquals(new HashSet<>(lista).size(), new TreeSet<>(lista).size());

        assertEquals(0, lap("7o").compareTo(lap("7o")));
        assertNotEquals(0, lap("7o").compareTo(lap("7t")));
        assertNotEquals(lap("7o"), lap("7t"));
    }

    @Test
    void nullKomponensNemEngedett() {
        assertThrows(NullPointerException.class, () -> new Kartya(null, Ertek.ASZ));
        assertThrows(NullPointerException.class, () -> new Kartya(Szin.PIKK, null));
    }
}
