package kartya;

import kartya.ertekeles.KezErtekelo;
import kartya.jatek.Jatek;
import kartya.jatek.Konzol;
import kartya.jatekos.EmberiJatekos;
import kartya.jatekos.GepiJatekos;
import kartya.jatekos.Jatekos;
import kartya.model.Pakli;
import kartya.strategia.KockazatosStrategia;
import kartya.strategia.OvatosStrategia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Belépési pont: csak a parancssori argumentumokat értelmezi és összerakja az objektumokat.
 * <p>
 * Használat: {@code java -jar poker.jar [seed] [magyar]}
 * <ul>
 *   <li>{@code seed} – egész szám; megadásával a játszma visszajátszható,</li>
 *   <li>{@code magyar} – 32 lapos pakli (7-estől ászig) az 52 lapos helyett.</li>
 * </ul>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Long seed = null;
        boolean magyar = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("magyar")) {
                magyar = true;
            } else {
                try {
                    seed = Long.parseLong(arg);
                } catch (NumberFormatException e) {
                    System.err.println("Ismeretlen argumentum: " + arg);
                    System.err.println("Használat: java -jar poker.jar [seed] [magyar]");
                    System.exit(1);
                }
            }
        }

        Random random = seed != null ? new Random(seed) : new Random();
        Supplier<Pakli> pakliGyar = magyar ? Pakli::magyar32 : Pakli::francia52;
        KezErtekelo ertekelo = new KezErtekelo(pakliGyar.get().legkisebbErtek());
        Konzol konzol = new Konzol(
                new BufferedReader(new InputStreamReader(System.in)),
                System.out,
                System.console() == null);

        List<Jatekos> jatekosok = List.of(
                new EmberiJatekos("Te", konzol),
                new GepiJatekos("Olga", new OvatosStrategia(ertekelo)),
                new GepiJatekos("Karcsi", new KockazatosStrategia(ertekelo)));

        konzol.kiir("5 lapos húzós póker — " + (magyar ? "32" : "52") + " lapos pakli"
                + (seed != null ? ", seed: " + seed : ""));
        new Jatek(jatekosok, pakliGyar, random, ertekelo, konzol).futtat();
    }
}
