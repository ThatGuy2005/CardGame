package kartya.jatekos;

import kartya.model.Kartya;
import kartya.model.Kez;

import java.util.Objects;
import java.util.Set;

/**
 * Egy játékos: neve és keze van, és el tudja dönteni, mely lapjait cseréli.
 * <p>
 * Sealed: a játékmenet pontosan két fajta játékossal számol (gépi és emberi), így a
 * {@code switch} mintaillesztés {@code default} ág nélkül is teljes.
 * <p>
 * Az {@code equals}/{@code hashCode} szándékosan az {@link Object}-től örökölt (identitás alapú):
 * két azonos nevű játékos is két különböző résztvevő, és a játékos állapota (keze) változik, ami
 * állapot-alapú hashCode esetén elrontaná a {@code HashMap} kulcsként való használatot.
 */
public sealed abstract class Jatekos permits GepiJatekos, EmberiJatekos {

    private final String nev;
    private final Kez kez = new Kez();

    protected Jatekos(String nev) {
        this.nev = Objects.requireNonNull(nev, "A név nem lehet null");
    }

    public String nev() {
        return nev;
    }

    public Kez kez() {
        return kez;
    }

    /** A cserélendő lapok kiválasztása az aktuális kézből. */
    public abstract Set<Kartya> eldobandok();

    @Override
    public String toString() {
        return nev;
    }
}
