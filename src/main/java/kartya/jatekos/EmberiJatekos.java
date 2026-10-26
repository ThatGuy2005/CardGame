package kartya.jatekos;

import kartya.jatek.Konzol;
import kartya.model.Kartya;
import kartya.strategia.Strategia;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/** A konzolon ülő játékos: a cserélendő lapokat sorszám alapján adja meg. */
public final class EmberiJatekos extends Jatekos {

    private final Konzol konzol;

    public EmberiJatekos(String nev, Konzol konzol) {
        super(nev);
        this.konzol = Objects.requireNonNull(konzol);
    }

    @Override
    public Set<Kartya> eldobandok() {
        List<Kartya> lapok = kez().lapok();
        konzol.kiir("Lapjaid:");
        for (int i = 0; i < lapok.size(); i++) {
            konzol.kiir("  " + (i + 1) + ") " + lapok.get(i));
        }

        while (true) {
            Optional<String> valasz = konzol.kerdez(
                    "Cserélendő lapok sorszámai (pl. 1 3 5, legfeljebb " + Strategia.MAX_ELDOBAS
                            + "), Enter = nincs csere: ");
            if (valasz.isEmpty()) {
                konzol.kiir("Nincs több bemenet — nem cserélsz lapot.");
                return Set.of();
            }
            try {
                return sorszamokFeldolgozasa(valasz.get(), lapok.size()).stream()
                        .map(sorszam -> lapok.get(sorszam - 1))
                        .collect(Collectors.toUnmodifiableSet());
            } catch (IllegalArgumentException e) {
                konzol.kiir("Hibás bemenet: " + e.getMessage() + " Próbáld újra!");
            }
        }
    }

    /**
     * A felhasználó által beírt sor értelmezése 1-től induló sorszámok halmazává.
     * Elválasztó lehet szóköz vagy vessző; az üres sor üres halmazt jelent.
     *
     * @throws IllegalArgumentException ha a sor nem szám, tartományon kívüli, ismétlődik,
     *                                  vagy túl sok sorszámot tartalmaz
     */
    static Set<Integer> sorszamokFeldolgozasa(String sor, int lapszam) {
        Set<Integer> sorszamok = new TreeSet<>();
        if (sor.isBlank()) {
            return sorszamok;
        }
        for (String resz : sor.strip().split("[\\s,]+")) {
            int sorszam;
            try {
                sorszam = Integer.parseInt(resz);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("„" + resz + "” nem szám.");
            }
            if (sorszam < 1 || sorszam > lapszam) {
                throw new IllegalArgumentException("A sorszám 1 és " + lapszam + " között lehet, kapott: " + sorszam + ".");
            }
            if (!sorszamok.add(sorszam)) {
                throw new IllegalArgumentException("A(z) " + sorszam + ". lap kétszer szerepel.");
            }
        }
        if (sorszamok.size() > Strategia.MAX_ELDOBAS) {
            throw new IllegalArgumentException("Legfeljebb " + Strategia.MAX_ELDOBAS + " lapot cserélhetsz.");
        }
        return sorszamok;
    }
}
