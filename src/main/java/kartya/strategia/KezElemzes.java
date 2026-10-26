package kartya.strategia;

import kartya.model.Ertek;
import kartya.model.Kartya;
import kartya.model.Kez;
import kartya.model.Szin;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/** A stratégiák közös segédfüggvényei a kéz elemzéséhez. */
final class KezElemzes {

    private KezElemzes() {
    }

    /** Azok a lapok, amelyek értéke csak egyszer fordul elő a kézben (a „kísérők”). */
    static Set<Kartya> parositatlanok(Kez kez) {
        Map<Ertek, Long> gyakorisag = kez.lapok().stream()
                .collect(Collectors.groupingBy(Kartya::ertek, Collectors.counting()));
        return kez.lapok().stream()
                .filter(k -> gyakorisag.get(k.ertek()) == 1)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** A kéz {@code darab} legalacsonyabb lapja (természetes rendezés szerint). */
    static Set<Kartya> legalacsonyabbak(Kez kez, int darab) {
        return kez.lapok().stream()
                .sorted()
                .limit(darab)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Ha a kézben legalább {@code minimum} azonos színű lap van, visszaadja a többi (más színű)
     * lapot — ezeket kell eldobni a flush eléréséhez. Különben üres.
     */
    static Optional<Set<Kartya>> szinhuzas(Kez kez, int minimum) {
        return kez.csoportositSzinSzerint().entrySet().stream()
                .filter(e -> e.getValue().size() >= minimum)
                .map(Map.Entry::getKey)
                .findFirst()
                .map((Szin szin) -> kez.lapok().stream()
                        .filter(k -> k.szin() != szin)
                        .collect(Collectors.toUnmodifiableSet()));
    }

    /**
     * Ha a kéz 4 lapja sorhúzást alkot (4 különböző érték egy legfeljebb 5 széles sávban, tehát
     * nyitott vagy belső sorhúzás), visszaadja az ötödik, eldobandó lapot. Az ászt magas és — a
     * pakli legkisebb lapja alatt — alacsony lapként is figyelembe veszi.
     */
    static Optional<Kartya> sorhuzas(Kez kez, Ertek legkisebbErtek) {
        ToIntFunction<Ertek> aszMagas = Ertek::ordinal;
        ToIntFunction<Ertek> aszAlacsony = e -> e == Ertek.ASZ ? legkisebbErtek.ordinal() - 1 : e.ordinal();

        // Növekvő sorrendben próbálkozunk, így több lehetőség esetén a legkisebb lapot dobjuk.
        for (Kartya jelolt : kez.rendezve(Comparator.naturalOrder())) {
            List<Ertek> tobbi = kez.lapok().stream()
                    .filter(k -> !k.equals(jelolt))
                    .map(Kartya::ertek)
                    .distinct()
                    .toList();
            if (tobbi.size() == 4 && (szuksav(tobbi, aszMagas) || szuksav(tobbi, aszAlacsony))) {
                return Optional.of(jelolt);
            }
        }
        return Optional.empty();
    }

    private static boolean szuksav(List<Ertek> ertekek, ToIntFunction<Ertek> rang) {
        int min = ertekek.stream().mapToInt(rang).min().orElseThrow();
        int max = ertekek.stream().mapToInt(rang).max().orElseThrow();
        return max - min <= 4;
    }
}
