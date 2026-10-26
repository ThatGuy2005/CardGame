package kartya.jatek;

import kartya.ertekeles.KezErtek;
import kartya.ertekeles.KezErtekelo;
import kartya.jatekos.EmberiJatekos;
import kartya.jatekos.GepiJatekos;
import kartya.jatekos.Jatekos;
import kartya.model.Kartya;
import kartya.model.KartyaRendezesek;
import kartya.model.Kez;
import kartya.model.Pakli;
import kartya.strategia.Strategia;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Egyetlen kör lebonyolítása: osztás, csere, leterítés, nyertes(ek) megállapítása. */
final class Kor {

    private final List<Jatekos> jatekosok;
    private final Pakli pakli;
    private final KezErtekelo ertekelo;
    private final Konzol konzol;

    Kor(List<Jatekos> jatekosok, Pakli pakli, KezErtekelo ertekelo, Konzol konzol) {
        this.jatekosok = jatekosok;
        this.pakli = pakli;
        this.ertekelo = ertekelo;
        this.konzol = konzol;
    }

    KorEredmeny lejatszik() {
        osztas();
        jatekosok.forEach(this::csere);
        return leterites();
    }

    /** Körbeosztás: mindenki egyesével kap lapot, amíg 5 lapja nem lesz. */
    private void osztas() {
        jatekosok.forEach(j -> j.kez().urit());
        for (int i = 0; i < Kez.MAX_LAPSZAM; i++) {
            for (Jatekos jatekos : jatekosok) {
                jatekos.kez().hozzaad(pakli.huz());
            }
        }
    }

    private void csere(Jatekos jatekos) {
        Kez kez = jatekos.kez();
        kez.rendez(KartyaRendezesek.ERTEK_SZERINT);

        Set<Kartya> eldobandok = jatekos.eldobandok();
        ellenoriz(kez, eldobandok);
        eldobandok.forEach(kez::eltavolit);
        eldobandok.forEach(lap -> kez.hozzaad(pakli.huz()));
        kez.rendez(KartyaRendezesek.ERTEK_SZERINT);

        // A Jatekos sealed, ezért a switch default ág nélkül is teljes.
        String uzenet = switch (jatekos) {
            case EmberiJatekos ember -> "Új lapjaid: " + lapLista(kez.lapok());
            case GepiJatekos gep -> gep + " " + eldobandok.size() + " lapot cserélt.";
        };
        konzol.kiir(uzenet);
    }

    /** Védelem hibás (pl. rosszul megírt) stratégia ellen. */
    private static void ellenoriz(Kez kez, Set<Kartya> eldobandok) {
        if (eldobandok.size() > Strategia.MAX_ELDOBAS) {
            throw new IllegalStateException("Legfeljebb " + Strategia.MAX_ELDOBAS + " lap cserélhető: " + eldobandok);
        }
        if (!kez.lapok().containsAll(eldobandok)) {
            throw new IllegalStateException("Csak a kézben lévő lap dobható el: " + eldobandok);
        }
    }

    private KorEredmeny leterites() {
        konzol.kiir();
        konzol.kiir("--- Leterítés ---");

        // LinkedHashMap: a kiírás és a nyertesek sorrendje kövesse az ülésrendet.
        Map<Jatekos, KezErtek> ertekek = new LinkedHashMap<>();
        for (Jatekos jatekos : jatekosok) {
            KezErtek ertek = ertekelo.ertekel(jatekos.kez());
            ertekek.put(jatekos, ertek);
            List<Kartya> rendezett = jatekos.kez().rendezve(
                    KartyaRendezesek.pokerKiertekeles(jatekos.kez().lapok()));
            konzol.kiir(String.format("%-20s %-60s -> %s", jatekos, lapLista(rendezett), ertek));
        }

        KezErtek legjobb = Collections.max(ertekek.values());
        List<Jatekos> nyertesek = ertekek.entrySet().stream()
                .filter(e -> e.getValue().compareTo(legjobb) == 0)
                .map(Map.Entry::getKey)
                .toList();

        konzol.kiir((nyertesek.size() == 1 ? "Nyertes: " : "Holtverseny: ")
                + nyertesek.stream().map(Jatekos::nev).collect(Collectors.joining(", "))
                + " — " + legjobb.tipus());
        return new KorEredmeny(ertekek, nyertesek);
    }

    private static String lapLista(List<Kartya> lapok) {
        return lapok.stream().map(Kartya::toString).collect(Collectors.joining(", "));
    }
}
