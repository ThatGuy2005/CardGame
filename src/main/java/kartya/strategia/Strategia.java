package kartya.strategia;

import kartya.model.Kartya;
import kartya.model.Kez;

import java.util.Set;

/**
 * A gépi játékos döntési stratégiája (Strategy minta): eldönti, mely lapokat cserélje.
 * <p>
 * Szándékosan nem sealed: a Strategy minta lényege, hogy új stratégia a meglévő kód módosítása
 * nélkül hozzáadható.
 */
@FunctionalInterface
public interface Strategia {

    /** Egy körben legfeljebb ennyi lap cserélhető. */
    int MAX_ELDOBAS = 3;

    /**
     * @return az eldobandó lapok; legfeljebb {@value #MAX_ELDOBAS} elem, és mind a kéz része.
     *         Halmazt adunk vissza, mert ugyanazt a lapot kétszer eldobni értelmetlen.
     */
    Set<Kartya> eldobandok(Kez kez);
}
