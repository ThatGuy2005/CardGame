package kartya.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kartya.TesztLapok.lapok;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KartyaRendezesekTest {

    private static final List<Kartya> MINTA = lapok("10o Ap 3t Ko 3p At");

    private static List<Kartya> rendezve(Comparator<Kartya> rendezes) {
        List<Kartya> lista = new ArrayList<>(MINTA);
        lista.sort(rendezes);
        return lista;
    }

    @Test
    @DisplayName("SZIN_SZERINT: szín, azon belül érték növekvően")
    void szinSzerint() {
        assertEquals(lapok("3t At 10o Ko 3p Ap"), rendezve(KartyaRendezesek.SZIN_SZERINT));
    }

    @Test
    @DisplayName("ERTEK_SZERINT: érték csökkenően, azon belül szín")
    void ertekSzerint() {
        assertEquals(lapok("At Ap Ko 10o 3t 3p"), rendezve(KartyaRendezesek.ERTEK_SZERINT));
    }

    @Test
    @DisplayName("pokerKiertekeles: a gyakoribb érték előre, azon belül érték csökkenően")
    void pokerKiertekeles() {
        List<Kartya> kez = lapok("4t Do Ap 4k Dt");
        List<Kartya> lista = new ArrayList<>(kez);
        lista.sort(KartyaRendezesek.pokerKiertekeles(kez));

        // Két pár: a dámapár, a négyespár, végül a kísérő ász.
        assertEquals(lapok("Dt Do 4t 4k Ap"), lista);
    }

    @Test
    @DisplayName("pokerKiertekeles: fullnál a drill megelőzi a nagyobb értékű párt")
    void pokerKiertekelesFull() {
        List<Kartya> kez = lapok("Ap 2t Ak 2o 2k");
        List<Kartya> lista = new ArrayList<>(kez);
        lista.sort(KartyaRendezesek.pokerKiertekeles(kez));

        assertEquals(lapok("2t 2k 2o Ak Ap"), lista);
    }
}
