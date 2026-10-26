package kartya.model;

import java.util.Comparator;
import java.util.Objects;

/**
 * Egy megváltoztathatatlan kártyalap.
 * <p>
 * A record automatikusan generálja az {@code equals}/{@code hashCode} metódusokat a két komponens
 * (szín, érték) alapján, ezért a lap biztonságosan használható {@code HashSet} elemként és
 * {@code HashMap} kulcsként.
 * <p>
 * A természetes rendezés: először érték, majd szín. Mivel a {@code compareTo} pontosan ugyanazt a
 * két mezőt hasonlítja, mint az {@code equals}, ezért {@code a.compareTo(b) == 0} akkor és csak
 * akkor, ha {@code a.equals(b)} — vagyis a rendezés <em>konzisztens az equals-szal</em>. Ez azért
 * fontos, mert a {@code TreeSet}/{@code TreeMap} a {@code compareTo}-t használja az egyenlőség
 * eldöntésére, a {@code HashSet} pedig az {@code equals}-t; ha a kettő eltérne, ugyanaz a lapkészlet
 * a két gyűjteményben különböző méretű lehetne.
 */
public record Kartya(Szin szin, Ertek ertek) implements Comparable<Kartya> {

    private static final Comparator<Kartya> TERMESZETES_RENDEZES =
            Comparator.comparing(Kartya::ertek).thenComparing(Kartya::szin);

    public Kartya {
        Objects.requireNonNull(szin, "A szín nem lehet null");
        Objects.requireNonNull(ertek, "Az érték nem lehet null");
    }

    @Override
    public int compareTo(Kartya masik) {
        return TERMESZETES_RENDEZES.compare(this, masik);
    }

    @Override
    public String toString() {
        return szin.megnevezes() + " " + ertek.megnevezes();
    }
}
