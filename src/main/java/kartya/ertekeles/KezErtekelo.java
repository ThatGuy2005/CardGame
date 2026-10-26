package kartya.ertekeles;

import kartya.model.Ertek;
import kartya.model.Kartya;
import kartya.model.KartyaRendezesek;
import kartya.model.Kez;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Egy 5 lapos kéz kiértékelése {@link KezErtek}-ké.
 * <p>
 * A „kis sor” (az ász alacsony lapként) a pakli legkisebb lapjától függ: 52 lapnál A-2-3-4-5,
 * 32 lapnál A-7-8-9-10. Ezért a kiértékelő megkapja a pakli legkisebb értékét.
 */
public final class KezErtekelo {

    private final Ertek legkisebbErtek;
    private final Set<Ertek> kisSor;
    private final Ertek kisSorLegnagyobb;

    /** Kiértékelő az 52 lapos paklihoz. */
    public KezErtekelo() {
        this(Ertek.KETTO);
    }

    public KezErtekelo(Ertek legkisebbErtek) {
        this.legkisebbErtek = Objects.requireNonNull(legkisebbErtek);
        this.kisSorLegnagyobb = Ertek.values()[legkisebbErtek.ordinal() + 3];
        this.kisSor = EnumSet.range(legkisebbErtek, kisSorLegnagyobb);
        this.kisSor.add(Ertek.ASZ);
    }

    public KezErtek ertekel(Kez kez) {
        return ertekel(kez.lapok());
    }

    public KezErtek ertekel(Collection<Kartya> lapok) {
        if (lapok.size() != Kez.MAX_LAPSZAM) {
            throw new IllegalArgumentException("Csak " + Kez.MAX_LAPSZAM + " lapos kéz értékelhető, kapott: " + lapok.size());
        }

        // A póker-sorrend miatt a gyakoribb értékek kerülnek előre, így az egyedi értékek listája
        // egyben a holtversenyt eldöntő sorrend is (pl. pár, majd a kísérők csökkenően).
        List<Ertek> dontoErtekek = lapok.stream()
                .sorted(KartyaRendezesek.pokerKiertekeles(lapok))
                .map(Kartya::ertek)
                .distinct()
                .toList();

        String gyakorisagMinta = gyakorisagMinta(lapok);
        boolean flush = lapok.stream().map(Kartya::szin).distinct().count() == 1;
        Ertek sorTeteje = sorLegnagyobbLapja(dontoErtekek);
        boolean sor = sorTeteje != null;

        KezTipus tipus = switch (gyakorisagMinta) {
            case "41" -> KezTipus.POKER;
            case "32" -> KezTipus.FULL;
            case "311" -> KezTipus.DRILL;
            case "221" -> KezTipus.KET_PAR;
            case "2111" -> KezTipus.EGY_PAR;
            default -> {
                if (sor && flush) {
                    yield KezTipus.FLUSH_SOR;
                } else if (flush) {
                    yield KezTipus.FLUSH;
                } else if (sor) {
                    yield KezTipus.SOR;
                }
                yield KezTipus.MAGAS_LAP;
            }
        };

        // Sornál csak a legmagasabb lap számít (a kis sornál ez nem az ász!).
        if (tipus == KezTipus.SOR || tipus == KezTipus.FLUSH_SOR) {
            return new KezErtek(tipus, List.of(sorTeteje));
        }
        return new KezErtek(tipus, dontoErtekek);
    }

    public Ertek legkisebbErtek() {
        return legkisebbErtek;
    }

    /** Az értékgyakoriságok csökkenő sorrendben, összefűzve, pl. full esetén "32", két párnál "221". */
    private static String gyakorisagMinta(Collection<Kartya> lapok) {
        Map<Ertek, Long> gyakorisag = lapok.stream()
                .collect(Collectors.groupingBy(Kartya::ertek, Collectors.counting()));
        return gyakorisag.values().stream()
                .sorted((a, b) -> Long.compare(b, a))
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    /**
     * Ha a csökkenően rendezett, egyedi értékek sort alkotnak, visszaadja a sor legmagasabb lapját,
     * különben {@code null}-t.
     */
    private Ertek sorLegnagyobbLapja(List<Ertek> csokkenoErtekek) {
        if (csokkenoErtekek.size() != Kez.MAX_LAPSZAM) {
            return null;
        }
        Ertek legnagyobb = csokkenoErtekek.getFirst();
        Ertek legkisebb = csokkenoErtekek.getLast();
        if (legnagyobb.ordinal() - legkisebb.ordinal() == Kez.MAX_LAPSZAM - 1) {
            return legnagyobb;
        }
        if (Set.copyOf(csokkenoErtekek).equals(kisSor)) {
            return kisSorLegnagyobb;
        }
        return null;
    }
}
