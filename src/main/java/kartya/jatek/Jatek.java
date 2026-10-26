package kartya.jatek;

import kartya.ertekeles.KezErtekelo;
import kartya.jatekos.Jatekos;
import kartya.model.Kez;
import kartya.model.Pakli;
import kartya.strategia.Strategia;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/** A játszma: körök ismétlése és a pontok vezetése, amíg a játékos ki nem lép. */
public final class Jatek {

    private final List<Jatekos> jatekosok;
    private final Supplier<Pakli> pakliGyar;
    private final Random random;
    private final KezErtekelo ertekelo;
    private final Konzol konzol;
    private final Eredmenytabla tabla;

    /**
     * @param pakliGyar minden körben új paklit ad (pl. {@code Pakli::francia52})
     * @param random    a keverés véletlenforrása; seedelve a játszma visszajátszható
     */
    public Jatek(List<Jatekos> jatekosok, Supplier<Pakli> pakliGyar, Random random,
                 KezErtekelo ertekelo, Konzol konzol) {
        this.jatekosok = List.copyOf(jatekosok);
        this.pakliGyar = Objects.requireNonNull(pakliGyar);
        this.random = Objects.requireNonNull(random);
        this.ertekelo = Objects.requireNonNull(ertekelo);
        this.konzol = Objects.requireNonNull(konzol);
        this.tabla = new Eredmenytabla(this.jatekosok);

        // Legrosszabb eset: mindenki 5 lapot kap és 3-at cserél.
        int maxLapigeny = this.jatekosok.size() * (Kez.MAX_LAPSZAM + Strategia.MAX_ELDOBAS);
        if (this.jatekosok.size() < 2 || maxLapigeny > pakliGyar.get().meret()) {
            throw new IllegalArgumentException("Ehhez a paklihoz túl kevés vagy túl sok játékos: " + this.jatekosok.size());
        }
    }

    public void futtat() {
        int korSzam = 1;
        do {
            konzol.kiir();
            konzol.kiir("===== " + korSzam + ". kör =====");
            Pakli pakli = pakliGyar.get();
            pakli.kever(random);

            KorEredmeny eredmeny = new Kor(jatekosok, pakli, ertekelo, konzol).lejatszik();
            eredmeny.nyertesek().forEach(tabla::pontotAd);

            konzol.kiir();
            konzol.kiir("Állás " + korSzam + " kör után:");
            tabla.sorok().forEach(konzol::kiir);
            korSzam++;
        } while (ujKor());

        konzol.kiir("Köszönöm a játékot!");
    }

    public Eredmenytabla tabla() {
        return tabla;
    }

    private boolean ujKor() {
        while (true) {
            Optional<String> valasz = konzol.kerdez("Új kör? (i/n): ");
            if (valasz.isEmpty()) {
                return false;
            }
            switch (valasz.get().toLowerCase()) {
                case "i", "igen" -> {
                    return true;
                }
                case "n", "nem" -> {
                    return false;
                }
                default -> konzol.kiir("Kérlek, „i” vagy „n” betűvel válaszolj.");
            }
        }
    }
}
