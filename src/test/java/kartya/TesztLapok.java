package kartya;

import kartya.model.Ertek;
import kartya.model.Kartya;
import kartya.model.Kez;
import kartya.model.Szin;

import java.util.Arrays;
import java.util.List;

/**
 * Tömör lapjelölés a tesztekhez: érték (2–10, B, D, K, A) + szín betűje
 * (t = treff, k = káró, o = kőr, p = pikk). Pl. {@code "Kk"} = káró király, {@code "10p"} = pikk 10.
 */
public final class TesztLapok {

    private TesztLapok() {
    }

    public static Kartya lap(String jeloles) {
        Szin szin = switch (jeloles.charAt(jeloles.length() - 1)) {
            case 't' -> Szin.TREFF;
            case 'k' -> Szin.KARO;
            case 'o' -> Szin.KOR;
            case 'p' -> Szin.PIKK;
            default -> throw new IllegalArgumentException("Ismeretlen szín: " + jeloles);
        };
        String ertekResz = jeloles.substring(0, jeloles.length() - 1);
        Ertek ertek = switch (ertekResz) {
            case "B" -> Ertek.BUBI;
            case "D" -> Ertek.DAMA;
            case "K" -> Ertek.KIRALY;
            case "A" -> Ertek.ASZ;
            default -> Arrays.stream(Ertek.values())
                    .filter(e -> e.szamErtek() == Integer.parseInt(ertekResz))
                    .findFirst()
                    .orElseThrow();
        };
        return new Kartya(szin, ertek);
    }

    /** Szóközzel elválasztott lapjelölések listává alakítása. */
    public static List<Kartya> lapok(String jelolesek) {
        return Arrays.stream(jelolesek.split(" ")).map(TesztLapok::lap).toList();
    }

    public static Kez kez(String jelolesek) {
        Kez kez = new Kez();
        lapok(jelolesek).forEach(kez::hozzaad);
        return kez;
    }
}
