package kartya.jatek;

import kartya.ertekeles.KezErtek;
import kartya.jatekos.Jatekos;

import java.util.List;
import java.util.Map;

/**
 * Egy lejátszott kör eredménye: minden játékos kézértéke és a nyertes(ek).
 * Holtverseny esetén több nyertes is lehet.
 */
public record KorEredmeny(Map<Jatekos, KezErtek> kezErtekek, List<Jatekos> nyertesek) {

    public KorEredmeny {
        kezErtekek = Map.copyOf(kezErtekek);
        nyertesek = List.copyOf(nyertesek);
    }
}
