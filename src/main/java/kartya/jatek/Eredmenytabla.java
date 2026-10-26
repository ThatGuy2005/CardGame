package kartya.jatek;

import kartya.jatekos.Jatekos;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pontszámok nyilvántartása. {@link LinkedHashMap}: O(1) frissítés játékos szerint, és
 * pontegyenlőségnél a kiírás az ülésrendet követi (a beszúrási sorrendet megőrzi).
 */
public final class Eredmenytabla {

    private final Map<Jatekos, Integer> pontok = new LinkedHashMap<>();

    public Eredmenytabla(List<Jatekos> jatekosok) {
        jatekosok.forEach(j -> pontok.put(j, 0));
    }

    public void pontotAd(Jatekos jatekos) {
        pontok.merge(jatekos, 1, Integer::sum);
    }

    public int pontszam(Jatekos jatekos) {
        return pontok.getOrDefault(jatekos, 0);
    }

    /** A tabella sorai pontszám szerint csökkenően (a rendezés stabil, így egyenlőségnél az ülésrend marad). */
    public List<String> sorok() {
        return pontok.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> String.format("  %-20s %d pont", e.getKey().nev(), e.getValue()))
                .toList();
    }
}
