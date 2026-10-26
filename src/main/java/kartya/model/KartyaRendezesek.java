package kartya.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A természetes rendezéstől eltérő, alternatív lapsorrendek gyűjteménye.
 * <p>
 * A {@link Comparable} csak egyetlen „alapértelmezett” sorrendet ad; minden további szempontot
 * külső {@link Comparator}-ként írunk meg, így a {@link Kartya} osztályt nem kell módosítani.
 */
public final class KartyaRendezesek {

    /** Szín szerint, azon belül érték szerint növekvően (pl. kézben a színek egymás mellé kerülnek). */
    public static final Comparator<Kartya> SZIN_SZERINT =
            Comparator.comparing(Kartya::szin)
                    .thenComparing(Kartya::ertek);

    /** Érték szerint csökkenően (ász elöl), egyenlő értéknél szín szerint. */
    public static final Comparator<Kartya> ERTEK_SZERINT =
            Comparator.comparing(Kartya::ertek, Comparator.reverseOrder())
                    .thenComparing(Kartya::szin);

    private KartyaRendezesek() {
    }

    /**
     * Póker-kiértékelési sorrend: előre kerülnek a gyakrabban előforduló értékek (pl. a pár a
     * kísérőlapok elé, fullnál a drill a pár elé), azonos gyakoriságon belül a nagyobb érték.
     * <p>
     * Ez nem lehet statikus konstans, mert a sorrend a <em>konkrét kéz</em> értékgyakoriságaitól
     * függ — ugyanaz a Király az egyik kézben pár része, a másikban csak kísérőlap. Ezért gyártó
     * metódus, amely a megadott lapokhoz épít összehasonlítót.
     *
     * @param lapok a kéz lapjai, amelyekből a gyakoriságokat számoljuk
     */
    public static Comparator<Kartya> pokerKiertekeles(Collection<Kartya> lapok) {
        Map<Ertek, Long> gyakorisag = lapok.stream()
                .collect(Collectors.groupingBy(Kartya::ertek,
                        () -> new EnumMap<>(Ertek.class),
                        Collectors.counting()));

        return Comparator.comparing((Kartya k) -> gyakorisag.getOrDefault(k.ertek(), 0L), Comparator.reverseOrder())
                .thenComparing(Kartya::ertek, Comparator.reverseOrder())
                .thenComparing(Kartya::szin);
    }
}
