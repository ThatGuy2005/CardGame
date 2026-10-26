package kartya.ertekeles;

/**
 * Pókerkéz-típusok a gyengétől az erősig. A deklarálás sorrendje az erősorrend, így az enum
 * természetes (ordinális) rendezése közvetlenül használható a kezek összehasonlítására.
 */
public enum KezTipus {
    MAGAS_LAP("Magas lap"),
    EGY_PAR("Egy pár"),
    KET_PAR("Két pár"),
    DRILL("Drill"),
    SOR("Sor"),
    FLUSH("Flush"),
    FULL("Full"),
    POKER("Póker"),
    FLUSH_SOR("Flush-sor");

    private final String megnevezes;

    KezTipus(String megnevezes) {
        this.megnevezes = megnevezes;
    }

    public String megnevezes() {
        return megnevezes;
    }

    @Override
    public String toString() {
        return megnevezes;
    }
}
