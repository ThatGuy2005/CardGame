package kartya.model;

/**
 * Lapértékek a kettestől az ászig. A deklarálás sorrendje megegyezik a numerikus értékek
 * sorrendjével, így az enum természetes rendezése egyben az erősorrend is.
 */
public enum Ertek {
    KETTO(2, "2"),
    HAROM(3, "3"),
    NEGY(4, "4"),
    OT(5, "5"),
    HAT(6, "6"),
    HET(7, "7"),
    NYOLC(8, "8"),
    KILENC(9, "9"),
    TIZ(10, "10"),
    BUBI(11, "Bubi"),
    DAMA(12, "Dáma"),
    KIRALY(13, "Király"),
    ASZ(14, "Ász");

    private final int szamErtek;
    private final String megnevezes;

    Ertek(int szamErtek, String megnevezes) {
        this.szamErtek = szamErtek;
        this.megnevezes = megnevezes;
    }

    public int szamErtek() {
        return szamErtek;
    }

    public String megnevezes() {
        return megnevezes;
    }

    @Override
    public String toString() {
        return megnevezes;
    }
}
