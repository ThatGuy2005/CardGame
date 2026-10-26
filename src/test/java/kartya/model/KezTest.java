package kartya.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static kartya.TesztLapok.kez;
import static kartya.TesztLapok.lap;
import static kartya.TesztLapok.lapok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KezTest {

    @Test
    @DisplayName("Keresés és eltávolítás")
    void keresesEsEltavolitas() {
        Kez kez = kez("Ap 7o Kt 7t 2k");

        assertTrue(kez.tartalmaz(lap("7o")));
        assertFalse(kez.tartalmaz(lap("7k")));

        assertTrue(kez.eltavolit(new Kartya(Szin.KOR, Ertek.HET)));
        assertFalse(kez.tartalmaz(lap("7o")));
        assertFalse(kez.eltavolit(lap("7o")), "Másodszor már nincs mit eltávolítani");
        assertEquals(4, kez.meret());
    }

    @Test
    @DisplayName("Duplikált lap és hatodik lap nem adható hozzá")
    void hozzaadasKorlatai() {
        Kez kez = kez("Ap 7o Kt 7t");
        assertThrows(IllegalArgumentException.class, () -> kez.hozzaad(lap("Ap")));

        kez.hozzaad(lap("2k"));
        assertThrows(IllegalStateException.class, () -> kez.hozzaad(lap("3k")));
    }

    @Test
    @DisplayName("Csoportosítás szín szerint EnumMap-be, a színek deklarálási sorrendjében")
    void csoportositasSzinSzerint() {
        Kez kez = kez("Ap 7o Kp 7t 2p");
        Map<Szin, List<Kartya>> csoportok = kez.csoportositSzinSzerint();

        assertInstanceOf(EnumMap.class, csoportok);
        assertEquals(List.of(Szin.TREFF, Szin.KOR, Szin.PIKK), List.copyOf(csoportok.keySet()));
        assertEquals(lapok("Ap Kp 2p"), csoportok.get(Szin.PIKK));
        assertEquals(lapok("7t"), csoportok.get(Szin.TREFF));
        assertFalse(csoportok.containsKey(Szin.KARO));
    }

    @Test
    @DisplayName("rendezve() új listát ad, a kéz sorrendje változatlan; rendez() helyben rendez")
    void rendezes() {
        Kez kez = kez("7o Ap 2k");

        assertEquals(lapok("Ap 7o 2k"), kez.rendezve(KartyaRendezesek.ERTEK_SZERINT));
        assertEquals(lapok("7o Ap 2k"), kez.lapok());

        kez.rendez(KartyaRendezesek.SZIN_SZERINT);
        assertEquals(lapok("2k 7o Ap"), kez.lapok());
    }
}
