package kartya.ertekeles;

import kartya.model.Ertek;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Egy kiértékelt kéz ereje: a kéz típusa és a holtverseny eldöntéséhez szükséges értékek
 * fontossági sorrendben (pl. egy pár esetén először a pár értéke, utána a kísérők csökkenően).
 * <p>
 * Szándékosan <em>nem</em> tároljuk magukat a lapokat: két kéz, amely csak színekben tér el, a
 * pókerben egyforma erős. Ha a lapok is a record részei lennének, akkor {@code compareTo == 0}
 * mellett {@code equals == false} lehetne, vagyis a rendezés nem lenne konzisztens az equals-szal.
 */
public record KezErtek(KezTipus tipus, List<Ertek> dontoErtekek) implements Comparable<KezErtek> {

    /** Két értéklista lexikografikus összehasonlítása (az első eltérő elem dönt). */
    private static final Comparator<List<Ertek>> LEXIKOGRAFIKUS = (a, b) -> {
        for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
            int eredmeny = a.get(i).compareTo(b.get(i));
            if (eredmeny != 0) {
                return eredmeny;
            }
        }
        return Integer.compare(a.size(), b.size());
    };

    private static final Comparator<KezErtek> RENDEZES =
            Comparator.comparing(KezErtek::tipus)
                    .thenComparing(KezErtek::dontoErtekek, LEXIKOGRAFIKUS);

    public KezErtek {
        Objects.requireNonNull(tipus, "A kéz típusa nem lehet null");
        dontoErtekek = List.copyOf(dontoErtekek);
    }

    @Override
    public int compareTo(KezErtek masik) {
        return RENDEZES.compare(this, masik);
    }

    @Override
    public String toString() {
        return tipus.megnevezes() + " (" + dontoErtekek.stream()
                .map(Ertek::megnevezes)
                .collect(Collectors.joining(", ")) + ")";
    }
}
