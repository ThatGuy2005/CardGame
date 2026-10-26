package kartya.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Kártyapakli.
 * <p>
 * Belül {@link Deque}-ként ({@link ArrayDeque}) tároljuk a lapokat: a húzás mindig a pakli
 * tetejéről történik, ami O(1) művelet, és a Deque interfész pontosan ezt a „verem tetejéről
 * veszünk” szándékot fejezi ki. Index szerinti elérésre nincs szükség, ezért nem List.
 * <p>
 * A 32 lapos pakli is a francia enumokból épül (7-estől ászig): így egyetlen {@link Kartya}
 * típus, egy kiértékelő és ugyanazok az összehasonlítók szolgálják ki mindkét paklit.
 */
public final class Pakli {

    private final Deque<Kartya> lapok;
    private final Ertek legkisebbErtek;

    private Pakli(Ertek legkisebbErtek) {
        this.legkisebbErtek = legkisebbErtek;
        this.lapok = new ArrayDeque<>();
        for (Szin szin : Szin.values()) {
            for (Ertek ertek : EnumSet.range(legkisebbErtek, Ertek.ASZ)) {
                lapok.addLast(new Kartya(szin, ertek));
            }
        }
    }

    /** Teljes, 52 lapos francia pakli (2-estől ászig), rendezett sorrendben. */
    public static Pakli francia52() {
        return new Pakli(Ertek.KETTO);
    }

    /** 32 lapos pakli (7-estől ászig, a magyar kártya lapszámának megfelelően), rendezett sorrendben. */
    public static Pakli magyar32() {
        return new Pakli(Ertek.HET);
    }

    /**
     * Megkeveri a paklit. A véletlenforrást kívülről kapjuk, így azonos seed esetén a keverés
     * eredménye is azonos (determinisztikus tesztek, visszajátszható játszmák).
     */
    public void kever(Random random) {
        // A Collections.shuffle List-et vár, ezért ideiglenesen listába másoljuk a lapokat.
        List<Kartya> lista = new ArrayList<>(lapok);
        Collections.shuffle(lista, random);
        lapok.clear();
        lapok.addAll(lista);
    }

    /** Leveszi és visszaadja a pakli legfelső lapját. */
    public Kartya huz() {
        if (lapok.isEmpty()) {
            throw new NoSuchElementException("A pakli üres, nem lehet több lapot húzni");
        }
        return lapok.pollFirst();
    }

    public int meret() {
        return lapok.size();
    }

    public boolean ures() {
        return lapok.isEmpty();
    }

    /** A pakli legkisebb lapértéke (52 lapnál a 2-es, 32 lapnál a 7-es) — a „kis sor” felismeréséhez kell. */
    public Ertek legkisebbErtek() {
        return legkisebbErtek;
    }

    /** A lapok aktuális sorrendje felülről lefelé, módosíthatatlan másolatként. */
    public List<Kartya> lapok() {
        return List.copyOf(lapok);
    }
}
