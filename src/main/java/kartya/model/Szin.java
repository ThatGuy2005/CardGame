package kartya.model;

/**
 * Francia kártyaszínek. A deklarálás sorrendje adja a természetes (ordinális) rendezést:
 * TREFF &lt; KARO &lt; KOR &lt; PIKK (a bridzsben is használt sorrend).
 */
public enum Szin {
    TREFF("Treff"),
    KARO("Káró"),
    KOR("Kőr"),
    PIKK("Pikk");

    private final String megnevezes;

    Szin(String megnevezes) {
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
