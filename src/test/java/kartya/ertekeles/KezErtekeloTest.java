package kartya.ertekeles;

import kartya.model.Ertek;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static kartya.TesztLapok.kez;
import static kartya.TesztLapok.lapok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KezErtekeloTest {

    private final KezErtekelo ertekelo = new KezErtekelo();

    private KezErtek ertek(String lapok) {
        return ertekelo.ertekel(kez(lapok));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "9t 10t Bt Dt Kt, FLUSH_SOR",
            "9t 9k 9o 9p Kt,  POKER",
            "9t 9k 9o Kp Kt,  FULL",
            "2t 9t Bt Dt Kt,  FLUSH",
            "9t 10k Bo Dp Kt, SOR",
            "Ao 2t 3k 4t 5p,  SOR",
            "9t 9k 9o 2p Kt,  DRILL",
            "9t 9k 2o 2p Kt,  KET_PAR",
            "9t 9k 3o 2p Kt,  EGY_PAR",
            "9t 7k 3o 2p Kt,  MAGAS_LAP",
    })
    @DisplayName("Minden kéztípust felismer")
    void kezTipusok(String lapok, KezTipus vart) {
        assertEquals(vart, ertek(lapok.strip()).tipus());
    }

    @Test
    @DisplayName("A kéztípusok erősorrendje")
    void tipusokSorrendje() {
        List<KezErtek> csokkeno = List.of(
                ertek("9t 10t Bt Dt Kt"), ertek("9t 9k 9o 9p Kt"), ertek("9t 9k 9o Kp Kt"),
                ertek("2t 9t Bt Dt Kt"), ertek("9t 10k Bo Dp Kt"), ertek("9t 9k 9o 2p Kt"),
                ertek("9t 9k 2o 2p Kt"), ertek("9t 9k 3o 2p Kt"), ertek("9t 7k 3o 2p Kt"));
        for (int i = 0; i + 1 < csokkeno.size(); i++) {
            assertTrue(csokkeno.get(i).compareTo(csokkeno.get(i + 1)) > 0,
                    csokkeno.get(i) + " erősebb kell legyen, mint " + csokkeno.get(i + 1));
        }
    }

    @Test
    @DisplayName("Holtverseny-döntés: királypár veri a dámapárt, akkor is, ha annak ász kísérője van")
    void kiralyParVeriDamaPart() {
        assertTrue(ertek("Kt Kk 2o 3p 4t").compareTo(ertek("Dt Dk Ao 3p 4k")) > 0);
    }

    @Test
    @DisplayName("Holtverseny-döntés: azonos párnál a kísérők döntenek, csak színben eltérő kéz egyenlő")
    void kiseroDont() {
        assertTrue(ertek("Kt Kk Ao 3p 4t").compareTo(ertek("Ko Kp Dt 3k 4k")) > 0);
        assertTrue(ertek("Kt Kk 2o 3p 6t").compareTo(ertek("Ko Kp 2t 3k 5k")) > 0);

        KezErtek a = ertek("Kt Kk 2o 3p 6t");
        KezErtek b = ertek("Ko Kp 2t 3k 6k");
        assertEquals(0, a.compareTo(b));
        assertEquals(a, b, "compareTo == 0 esetén az equals is igaz");
    }

    @Test
    @DisplayName("Holtverseny-döntés: két párnál a nagyobb pár, fullnál a drill dönt")
    void ketParEsFull() {
        assertTrue(ertek("At Ak 2o 2p 3t").compareTo(ertek("Kt Kk Do Dp At")) > 0);
        assertTrue(ertek("3t 3k 3o 2p 2t").compareTo(ertek("2o 2k 2p At Ak")) > 0);
    }

    @Test
    @DisplayName("A kis sor (A-2-3-4-5) a leggyengébb sor")
    void kisSor() {
        KezErtek kisSor = ertek("Ao 2t 3k 4t 5p");
        assertEquals(List.of(Ertek.OT), kisSor.dontoErtekek());
        assertTrue(ertek("2o 3t 4k 5t 6p").compareTo(kisSor) > 0);
    }

    @Test
    @DisplayName("32 lapos paklinál A-7-8-9-10 sor, 52 lapos paklinál nem az")
    void kisSor32Lapos() {
        KezErtekelo magyar = new KezErtekelo(Ertek.HET);
        assertEquals(KezTipus.SOR, magyar.ertekel(lapok("Ao 7t 8k 9t 10p")).tipus());
        assertEquals(KezTipus.MAGAS_LAP, ertekelo.ertekel(lapok("Ao 7t 8k 9t 10p")).tipus());
    }

    @Test
    void csakOtLaposKezErtekelheto() {
        assertThrows(IllegalArgumentException.class, () -> ertekelo.ertekel(lapok("Ao 7t 8k 9t")));
    }
}
