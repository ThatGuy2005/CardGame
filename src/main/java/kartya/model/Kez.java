package kartya.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Egy játékos kezében lévő lapok.
 * <p>
 * Háttérgyűjtemény: {@link ArrayList}. Indoklás:
 * <ul>
 *   <li>a kéz legfeljebb 5 lapos, így a {@code contains}/{@code remove} O(n) költsége elhanyagolható,
 *       egy hash- vagy fa-alapú halmaz többletterhe itt nem térülne meg;</li>
 *   <li>a lapoknak stabil, index szerint elérhető sorrendje van — az emberi játékos a konzolon
 *       sorszám alapján választja ki az eldobandó lapokat;</li>
 *   <li>a megjelenítési sorrend bármikor átállítható egy {@link Comparator}-ral
 *       ({@link #rendez(Comparator)}), ami egy {@code TreeSet} rögzített rendezésével nem menne.</li>
 * </ul>
 * A duplikátumokat (ugyanaz a lap kétszer) a {@link #hozzaad(Kartya)} kézzel szűri ki.
 */
public final class Kez {

    public static final int MAX_LAPSZAM = 5;

    private final List<Kartya> lapok = new ArrayList<>(MAX_LAPSZAM);

    public void hozzaad(Kartya kartya) {
        Objects.requireNonNull(kartya, "A lap nem lehet null");
        if (lapok.size() >= MAX_LAPSZAM) {
            throw new IllegalStateException("A kézben legfeljebb " + MAX_LAPSZAM + " lap lehet");
        }
        if (lapok.contains(kartya)) {
            throw new IllegalArgumentException("A lap már a kézben van: " + kartya);
        }
        lapok.add(kartya);
    }

    /** Eltávolítja a lapot; {@code true}, ha a lap a kézben volt. */
    public boolean eltavolit(Kartya kartya) {
        return lapok.remove(kartya);
    }

    public boolean tartalmaz(Kartya kartya) {
        return lapok.contains(kartya);
    }

    public int meret() {
        return lapok.size();
    }

    public void urit() {
        lapok.clear();
    }

    /** A lapok aktuális sorrendjükben, módosíthatatlan nézetként. */
    public List<Kartya> lapok() {
        return Collections.unmodifiableList(lapok);
    }

    /** A kéz saját sorrendjének átrendezése (pl. megjelenítés előtt). */
    public void rendez(Comparator<Kartya> rendezes) {
        lapok.sort(rendezes);
    }

    /** A lapok új listában, a megadott sorrendben — a kéz maga nem változik. */
    public List<Kartya> rendezve(Comparator<Kartya> rendezes) {
        return lapok.stream().sorted(rendezes).toList();
    }

    /**
     * A lapok szín szerint csoportosítva. {@link EnumMap}-et használunk, mert enum kulcsoknál
     * tömb-alapú, gyors és a kulcsokat a deklarálás sorrendjében járja be (Treff, Káró, Kőr, Pikk).
     */
    public Map<Szin, List<Kartya>> csoportositSzinSzerint() {
        return lapok.stream()
                .collect(Collectors.groupingBy(Kartya::szin,
                        () -> new EnumMap<>(Szin.class),
                        Collectors.toList()));
    }

    @Override
    public String toString() {
        return lapok.toString();
    }
}
