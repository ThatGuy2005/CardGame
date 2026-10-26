package kartya.strategia;

import kartya.ertekeles.KezErtekelo;
import kartya.ertekeles.KezTipus;
import kartya.model.Kartya;
import kartya.model.Kez;

import java.util.Objects;
import java.util.Set;

/**
 * Óvatos stratégia: megtart minden kész kombinációt (pár vagy jobb), és csak a kísérőket cseréli.
 * Ha nincs semmije, a két legmagasabb lapot tartja meg.
 */
public final class OvatosStrategia implements Strategia {

    private final KezErtekelo ertekelo;

    public OvatosStrategia() {
        this(new KezErtekelo());
    }

    public OvatosStrategia(KezErtekelo ertekelo) {
        this.ertekelo = Objects.requireNonNull(ertekelo);
    }

    @Override
    public Set<Kartya> eldobandok(Kez kez) {
        KezTipus tipus = ertekelo.ertekel(kez).tipus();
        if (tipus.compareTo(KezTipus.SOR) >= 0) {
            // Sor vagy jobb: mind az 5 lap számít, nincs mit cserélni.
            return Set.of();
        }
        if (tipus == KezTipus.MAGAS_LAP) {
            return KezElemzes.legalacsonyabbak(kez, MAX_ELDOBAS);
        }
        // Egy pár / két pár / drill: a párosítatlan kísérők mennek (legfeljebb 3).
        return KezElemzes.parositatlanok(kez);
    }

    @Override
    public String toString() {
        return "óvatos";
    }
}
