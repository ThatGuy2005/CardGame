package kartya.jatekos;

import kartya.model.Kartya;
import kartya.strategia.Strategia;

import java.util.Objects;
import java.util.Set;

/** Számítógépes játékos, amely a döntést a kapott {@link Strategia}-ra bízza. */
public final class GepiJatekos extends Jatekos {

    private final Strategia strategia;

    public GepiJatekos(String nev, Strategia strategia) {
        super(nev);
        this.strategia = Objects.requireNonNull(strategia, "A stratégia nem lehet null");
    }

    public Strategia strategia() {
        return strategia;
    }

    @Override
    public Set<Kartya> eldobandok() {
        return strategia.eldobandok(kez());
    }

    @Override
    public String toString() {
        return nev() + " [" + strategia + "]";
    }
}
