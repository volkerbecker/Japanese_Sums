/**
 *
 */
package de.amazingsax.japanese_sums;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author sax Enthaelt das Spielfeld bestehend aus Summenbloecken und
 *         Feldelementen, stellt methoden zur Loesung bereit
 */
public class Playfield extends Thread {

    final static float empiricalMemoryFactor = (float) 0.015; //abgeschaetzter Faktor zwischen Maximalem
    //heap und anzahl der Permutationen

    private ProgressDialog progress;
    Handler handler;
    boolean solveable;
    boolean solutionFound;
    boolean isToComplex;
    PlayfieldActivity context;


    /**
     * Erschafft ein Spielfeld
     *
     * @param context       Aufrufender Context
     * @param playfieldsize Größe des Spielfeldes
     * @param maxnumber     Maximalwert für Feldeintrag
     * @param ihblocks      Summenvorgaben horizontal
     * @param ivblocks      Summenvorgaben vertikal
     * @param sprogress     Progressbar zur Anzeige des Fortschritts (todo durch observer pattern ersetzen?)
     * @param shandler      Handler zur Kommunikatuion mit UI Thread (todo durch observer pattern ersetzen?)
     */
    public Playfield(PlayfieldActivity context, byte playfieldsize, byte maxnumber,
                     ArrayList<byte[]> ihblocks, ArrayList<byte[]> ivblocks,
                     ProgressDialog sprogress, Handler shandler) {
        super();

        this.context = context;
        handler = shandler;

        progress = sprogress;
        this.playfieldsize = playfieldsize;
        this.maxNumber = maxnumber;

        solveable = true;
        solutionFound = false;

        if (ihblocks.size() != playfieldsize
                || ivblocks.size() != playfieldsize) {
            throw new IllegalArgumentException(
                    "number of sum blogs must equal number of lines/collums");
        }
        Feldelement.setMaxnumber(maxnumber);
        Feld = new Feldelement[playfieldsize][playfieldsize];

        hblocks = ihblocks; // die uebergebenen Werte als Objektwerte nutzen
        vblocks = ivblocks;


        SumPermutations.setMaxNumber(this.maxNumber, this.playfieldsize);
        for (byte i = 0; i < playfieldsize; ++i) {
            for (byte j = 0; j < playfieldsize; ++j) {
                Feld[i][j] = new Feldelement(); // alle Feldelemente
                // "erschaffen"
            }

            // try {
            // vpertubations.add(new SumPermutations(vblocks.get(i))); // Die
            // moeglichen Permutationen vorberechnen
            // hpertubations.add(new SumPermutations(hblocks.get(i)));
            // }
            // catch (IllegalStateException e) {
            // solveable=false;
            // }
        }
    }

    /**
     * liest toComplex flag aus, true wenn Rätsel auf Gerät nicht gelöst werden kann
     *
     * @return
     */
    public boolean isToComplex() {
        return isToComplex;
    }

    public ArrayList<byte[]> gethBlocks() {
        return hblocks;
    }

    public ArrayList<byte[]> getvBlocks() {
        return vblocks;
    }


    /**
     * Konstruktor für Spielfeld ohne eintraege, wird zur erschaffung eines neuen Spielfeldes genutzt
     *
     * @param context       Aufrunfender Kontext
     * @param playfieldsize Spielfeldgröße
     * @param maxnumber     Maximalwert für Feldeinträge
     */
    public Playfield(PlayfieldActivity context, byte playfieldsize, byte maxnumber) {
        super();
        this.context = context;
        this.playfieldsize = playfieldsize;
        this.maxNumber = maxnumber;  // Spielfeldgroesse und Maximalwert setzen

        Feldelement.setMaxnumber(maxnumber);
        Feld = new Feldelement[playfieldsize][playfieldsize];


        SumPermutations.setMaxNumber(this.maxNumber, this.playfieldsize);
        for (byte i = 0; i < playfieldsize; ++i) {
            for (byte j = 0; j < playfieldsize; ++j) {
                Feld[i][j] = new Feldelement(); // alle Feldelemente
                // "erschaffen"
            }
        }
    }


    /**
     * Startet den Rätsellöser als Hintergrundthread
     */

    @Override
    public void run() {
        try {
            solutionFound = solve();
        } catch (IllegalStateException e) {
            solveable = false;
        }
        progress.dismiss();
        handler.sendMessage(new Message());
    }


    /**
     * Gibt solveable flag zurück,
     *
     * @return true wenn Rätsel lösbar, sonst false
     */
    public boolean isSolveable() {
        return solveable;
    }

    /**
     * Gibt Solved Flag zurück
     *
     * @return true: Rätsel ist schon gelöst, false: Rätsel ist noch nicht gelöst.
     */
    public boolean isSolved() {
        return solutionFound;
    }

    /**
     * Gibt Spielfeld auf Konsole aus
     * überbleibsel aus reiner Java implementierung ohne UI
     */
    @Deprecated
    public void displayPlayfield() {
        for (int i = 0; i < playfieldsize; ++i) {
            for (int j = 0; j < playfieldsize; ++j) {
                System.out.print(" "
                        + getFieldElement((byte) i, (byte) j).getValue());
                if (getFieldElement((byte) i, (byte) j).isFixed()) {
                    System.out.print(".|");
                } else {
                    System.out.print(" |");
                }
            }
            System.out.println("");
            for (int j = 0; j < playfieldsize; ++j) {
                System.out.print("----");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("");
    }

    /**
     * Gibt das Feldelent zurück an Position:
     *
     * @param zeile  Zeile
     * @param spalte Spalte
     * @return
     */
    public Feldelement getFieldElement(byte zeile, byte spalte) {
        return Feld[zeile][spalte];
    }

    public void setFixedValueAt(byte zeile, byte spalte, byte value) {
        if (value != 0) {
            for (byte i = 0; i < playfieldsize; ++i) {
                Feld[zeile][i].setValueImpossible(value);
                Feld[i][spalte].setValueImpossible(value);
            } // verbiete den wert in der gesamten Zeile und Spalte
        }
        Feld[zeile][spalte].setFixedValue(value); // Wert in der Zelle setzen,
        // Wert in dieser Zelle
        // wieder erlauben
        if (progress != null) {
            progress.incrementProgressBy(1);
        }
    }

    /**
     * Testet ob potentiell Lösungsmöglichkeit noch vorhanden
     * alle noch übrigen Permutationen aller Möglichen Zeilen/spalten die die Summen erfülllen werden getesetet
     * und gegenbenenfalls ausgeschlossen
     *
     * @param hv            Spaltzen oder Zeilen
     * @param line          Nummer der Spalte oder Zeile
     * @param hpertubations Liste mit noch nicht ausgeschlossenen Permutationen horizontal
     * @param vpertubations Liste mit noch nicht ausgeschlossenen permutationen Vertikal
     * @return
     */

    public boolean tryLines(boolean hv, int line, ArrayList<SumPermutations> hpertubations, ArrayList<SumPermutations> vpertubations) {
        SumPermutations lineToTest;
        boolean retvalue = false;

        ArrayList<boolean[]> inThislinepossible = new ArrayList<boolean[]>();
        for (int i = 0; i < playfieldsize; ++i) {
            inThislinepossible.add(new boolean[maxNumber + 1]);
        }

        if (hv)
            lineToTest = hpertubations.get(line);
        else
            lineToTest = vpertubations.get(line);

        byte[] recentpermutation = new byte[playfieldsize];

        int zeile = 0;
        int spalte = 0;
        //Schleife über alle Permutationen die für die Zeile/Spalte noch möglich sind
        while (lineToTest.getnextpermuation(recentpermutation)) {
            for (int i = 0; i < playfieldsize; ++i) {
                if (hv) {
                    zeile = line;
                    spalte = i;
                } else {
                    spalte = line;
                    zeile = i;
                } // Schleife über Zeile bzw. Spalte

                // Ist der Wert die Zelle an Position [zeile][spalte] noch möglich?
                if (!Feld[zeile][spalte].isValuePossible(recentpermutation[i])) {
                    lineToTest.removeRecentPermutation(); // wenn nicth kann diese Permutation aus der Liste entfernt werden
                    retvalue = true;
                    break; // weiter mit nächster Permutation
                } else {
                    inThislinepossible.get(i)[recentpermutation[i]] = true; // Ansonsten muss die Permutation weiter unterscuht werden
                }
            }
        }

        for (int i = 0; i < playfieldsize; ++i) {
            for (byte j = 0; j <= maxNumber; ++j) {
                if (!inThislinepossible.get(i)[j]) {
                    if (hv) {
                        zeile = line;
                        spalte = i;
                    } else {
                        spalte = line;
                        zeile = i;
                    }
                    if (Feld[zeile][spalte].isValuePossible(j)) {
                        // if(zeile==)
                        retvalue = true;
                        Feld[zeile][spalte].setValueImpossible(j);
                        byte[] wert = new byte[1];
                        if (Feld[zeile][spalte].isValueAlreadyFixed(wert)) {
                            this.setFixedValueAt((byte) zeile, (byte) spalte,
                                    wert[0]);
                        }
                    }

                }
            }
        }

        return retvalue;
    }

    /**
     * Löst das Rätsel
     *
     * @return true wenn Lösung gefunden, false wenn nicht
     * @throws IllegalStateException (wenn Gerät zu schwach zum finden der Lösung)
     */
    public boolean solve() throws IllegalStateException {
        ArrayList<SumPermutations> hpertubations = new ArrayList<SumPermutations>();
        ArrayList<SumPermutations> vpertubations = new ArrayList<SumPermutations>();
        boolean somthinChanges = false;
        int numberOFpermutations = 0;
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();

        //SumPermutations.resetOverAllNumberOfPermutations();

        vpertubations.clear();
        hpertubations.clear();

        if (progress != null) {
            handler.post(new Runnable() {

                             @Override
                             public void run() {
                                 context.progressdialog.setMessage(context.getResources().getString(
                                         R.string.progressPermut));
                             }
                         }
            );
            progress.setMax(2 * playfieldsize);
            progress.setProgress(0);
        }

        //Bestimme Anzahl der Permutationen
        for (byte i = 0; i < playfieldsize; ++i) {
            if (isInterrupted()) return false;
            SumPermutations tmpsumpermutation = new SumPermutations(vblocks.get(i));
            numberOFpermutations += tmpsumpermutation.determineNumberOfpermurtations();
            vpertubations.add(tmpsumpermutation);
            if (isInterrupted()) return false;
            tmpsumpermutation = new SumPermutations(hblocks.get(i));
            numberOFpermutations += tmpsumpermutation.determineNumberOfpermurtations();
            hpertubations.add(tmpsumpermutation);
        } //komplexitaet abschaetzen



        if (maxMemory * empiricalMemoryFactor < numberOFpermutations) {
            isToComplex = true;
            return false;
        } else isToComplex = false;

        // bestimme alle mit den Summenblöcken kompatiblen Permutationen
        for (byte i = 0; i < playfieldsize; ++i) {
            if (isInterrupted()) return false;
            vpertubations.get(i).calculateAllPermutyations();
            if (progress != null) {
                progress.incrementProgressBy(1);
            }
            if (isInterrupted()) return false;
            hpertubations.get(i).calculateAllPermutyations();
            if (progress != null) {
                progress.incrementProgressBy(1);
            }
        }


        if (progress != null) {
            handler.post(new Runnable() {

                             @Override
                             public void run() {
                                 context.progressdialog.setMessage(context.getResources().getString(
                                         R.string.progressdialog));
                             }
                         }
            );

            progress.setProgress(0);
            progress.setMax(playfieldsize * playfieldsize);
        }

        // Reduziere Lösungen solange sich was ändert
        do {
            somthinChanges = false;
            // Teste alle Zeilen und Spalten durch und sortiere die aus, die nicht mehr passen
            // Wenn mindestens eine Zeile aussortiert wurde, setze somethingchange auf true
            for (int i = 0; i < playfieldsize; ++i) {
                if (isInterrupted()) return false;
                somthinChanges |= tryLines(true, i, hpertubations, vpertubations);

                if (isInterrupted()) return false;
                somthinChanges |= tryLines(false, i, hpertubations, vpertubations);

            }

            // gucke ob jetzt in einem Feld nur noch ein Wert möglich ist
            // Wenn ja fixiere den Wert und update progress bar.
            for (int i = 0; i < playfieldsize; ++i) {
                for (int j = 0; j < playfieldsize; ++j) {
                    byte[] wert = new byte[1];
                    if (!Feld[i][j].isFixed()) {
                        if (Feld[i][j].isValueAlreadyFixed(wert)) {
                            this.setFixedValueAt((byte) i, (byte) j, wert[0]);
                            somthinChanges = true;
                        }
                    }
                }
            }

        } while (somthinChanges); // Füre die Schleife weiter aus bis sich nichts mehr ändert
        boolean retalue = true;
        for (int i = 0; i < playfieldsize; ++i) {
            for (int j = 0; j < playfieldsize; ++j) {
                retalue &= Feld[i][j].isFixed();
            }
        } // Teste ob alle Werte des Spielfeldes feststehen, also ob Lösung gefunden wurde
        return retalue;
    }


    /**
     * Gibt die Lösungsmatrix als byte array zurück
     * für Ausgabe in UI
     * todo könnte durch observer pattern ersetzt werden
     * @return Einträge im Spielfeld
     */
    public byte[][] getEntries() {
        byte[][] result = new byte[playfieldsize][playfieldsize];
        for (int i = 0; i < playfieldsize; ++i) {
            for (int j = 0; j < playfieldsize; ++j) {
                result[i][j] = Feld[i][j].getValue();
            }
        }
        return result;
    }

    // Member Variablen

    protected byte playfieldsize;
    protected byte maxNumber;
    protected Feldelement[][] Feld; // Array of fieldelements

    protected ArrayList<byte[]> hblocks;
    protected ArrayList<byte[]> vblocks;


    // private summen // hier bloecke fuer die Summen einfuegen.

}
