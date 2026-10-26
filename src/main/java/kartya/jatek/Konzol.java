package kartya.jatek;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Vékony réteg a konzolos be- és kimenet fölött. Egyetlen példány olvas a bemenetről, így nem
 * fordulhat elő, hogy két külön {@code Scanner} „ellopja” egymás elől a pufferelt sorokat.
 * <p>
 * Ha a bemenet nem interaktív (pl. fájlból átirányított), a beolvasott sort visszaírja a
 * kimenetre ({@code visszhang}), így a napló ugyanúgy néz ki, mint egy valódi játszma.
 */
public final class Konzol {

    private final BufferedReader be;
    private final PrintStream ki;
    private final boolean visszhang;

    public Konzol(BufferedReader be, PrintStream ki, boolean visszhang) {
        this.be = Objects.requireNonNull(be);
        this.ki = Objects.requireNonNull(ki);
        this.visszhang = visszhang;
    }

    public void kiir(String sor) {
        ki.println(sor);
    }

    public void kiir() {
        ki.println();
    }

    /**
     * Kiírja a kérdést, és beolvas egy sort.
     *
     * @return a beolvasott sor (levágott szóközökkel), vagy üres, ha a bemenet véget ért
     */
    public Optional<String> kerdez(String kerdes) {
        ki.print(kerdes);
        ki.flush();
        try {
            String sor = be.readLine();
            if (sor == null) {
                ki.println();
                return Optional.empty();
            }
            if (visszhang) {
                ki.println(sor);
            }
            return Optional.of(sor.strip());
        } catch (IOException e) {
            throw new UncheckedIOException("Hiba a bemenet olvasása közben", e);
        }
    }
}
