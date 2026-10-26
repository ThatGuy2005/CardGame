package kartya.strategia;

import kartya.ertekeles.KezErtekelo;
import kartya.ertekeles.KezTipus;
import kartya.model.Kartya;
import kartya.model.Kez;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Kockázatos stratégia: flush- és sorhúzásra hajt, akár egy meglévő pár feláldozásával is.
 * Prioritás:
 * <ol>
 *   <li>sor vagy jobb: nem cserél;</li>
 *   <li>4 azonos színű lap: a kilógó lapot cseréli (flushhúzás);</li>
 *   <li>4 lapos sorhúzás: a kilógó lapot cseréli;</li>
 *   <li>pár, két pár, drill: a kísérőket cseréli;</li>
 *   <li>3 azonos színű lap: a másik kettőt cseréli (hátsó ajtós flush);</li>
 *   <li>egyébként a két legmagasabb lapot tartja meg.</li>
 * </ol>
 */
public final class KockazatosStrategia implements Strategia {

    private final KezErtekelo ertekelo;

    public KockazatosStrategia() {
        this(new KezErtekelo());
    }

    public KockazatosStrategia(KezErtekelo ertekelo) {
        this.ertekelo = Objects.requireNonNull(ertekelo);
    }

    @Override
    public Set<Kartya> eldobandok(Kez kez) {
        KezTipus tipus = ertekelo.ertekel(kez).tipus();
        if (tipus.compareTo(KezTipus.SOR) >= 0) {
            return Set.of();
        }

        Optional<Set<Kartya>> flushHuzas = KezElemzes.szinhuzas(kez, 4);
        if (flushHuzas.isPresent()) {
            return flushHuzas.get();
        }

        Optional<Kartya> sorHuzas = KezElemzes.sorhuzas(kez, ertekelo.legkisebbErtek());
        if (sorHuzas.isPresent()) {
            return Set.of(sorHuzas.get());
        }

        if (tipus != KezTipus.MAGAS_LAP) {
            return KezElemzes.parositatlanok(kez);
        }

        return KezElemzes.szinhuzas(kez, 3)
                .orElseGet(() -> KezElemzes.legalacsonyabbak(kez, MAX_ELDOBAS));
    }

    @Override
    public String toString() {
        return "kockázatos";
    }
}
