package de.amazingsax.japanese_sums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

import android.util.Log;

/**
 * erzeugt alle Permutastionen fuer eine Zeile Spalte mit gegebenen
 * summenbloecken
 *
 * @author sax
 */
public class SumPermutations {
    final String DEBUG_TAG = "amazing";
    int numberOfPermutations;
    ArrayList<ArrayList<byte[]>> compatibleBlogs = null;
    int maxNumberOfPlaces;
    ListIterator<byte[]> recentPermutation;

    static private byte maxnumber, lineSize;
    private byte[] sums; // enthaelt die Summen
    LinkedList<byte[]> zeilen; // enthaelt alle moeglichen Zeilen fuer die
    // gegebenen Summen


    public static void setMaxNumber(byte _maxnumber, byte _playfieldsize) {
        maxnumber = _maxnumber;
        lineSize = _playfieldsize;
    }

    public SumPermutations(byte[] sumsin) {
        if (maxnumber == 0) {
            throw new IllegalStateException(
                    "Call setMaxnumber(byte maxnumber) before creating a sumPermutatin");
        }
        sums = sumsin.clone();
        //calculateAllPermutyations(); // alle permutationen vorberechnen
        //recentPermutation = zeilen.listIterator(); // iterator auf erste
        // permutation
    }

    public int getSize() {
        return zeilen.size();
    }

    public boolean getnextpermuation(byte[] nextpermutation) {
        if (recentPermutation.hasNext()) {
            byte[] tmp = recentPermutation.next();
            for (int i = 0; i < lineSize; ++i) {
                nextpermutation[i] = tmp[i];
            }
            return true;
        } else {
            recentPermutation = zeilen.listIterator(); // Iterator wieder auf
            // den Anfang setzen
            return false;
        }

    }

    public boolean removeRecentPermutation() {
        recentPermutation.remove();
        return true;
    }

    private byte qsum(byte[] folge) {
        byte result = 0;
        for (int i = 0; i < folge.length; ++i) {
            result += folge[i];
        }
        return result;
    }

    private void increment(byte[] M, int i) {
        if (i > 0) {
            if (M[i] + 1 < M[i - 1]) {
                ++M[i];
            } else {
                M[i] = 0;
                this.increment(M, i - 1);
            }
        } else {
            ++M[i];
        }
    }

    private boolean incrementindex(int[] index,
                                   ArrayList<ArrayList<byte[]>> posibleblogs) {
        // vector<int>::reverse_iterator iter1 = index.rbegin();
        int[] mnb = new int[index.length];
        boolean nichtfertig;

        for (int i = 0; i < mnb.length; ++i) {
            mnb[i] = posibleblogs.get(i).size();
        }

        int i = mnb.length - 1;

        do {
            if (index[i] < mnb[i] - 1 || i == 0) {
                nichtfertig = false;
                ++index[i];
            } else {
                nichtfertig = true;
                index[i] = 0;
                --i;
            }
        } while (nichtfertig);
        if (index[0] >= mnb[0])
            return false;
        else
            return true;
    }

    /**
     * Gibt die Möglichen Zahlenkombinationen für vorgegebene Summe zurück
     *
     * @param summe Summe
     * @return Liste mit Zahlen die die Summe ergeben
     */
    private ArrayList<byte[]> possibleNumbers(int summe) {
        return possibleNumbers(summe, maxnumber);
    }

    private ArrayList<byte[]> possibleNumbers(int summe, int maxnumber) {
        byte[] M;
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        ArrayList<Byte> tmp = new ArrayList<Byte>();

        result.clear();

        M = new byte[maxnumber];
        M[0] = 0;
        while (M[0] <= maxnumber) {
            int momSumme = qsum(M);
            if (summe == momSumme) {
                tmp.clear();
                for (int j = 0; j < M.length; ++j) {
                    if (M[j] != 0) {
                        tmp.add(M[j]);
                    }
                }
                Collections.sort(tmp);
                byte[] tmpar = new byte[tmp.size()];
                for (byte i = 0; i < tmpar.length; ++i) {
                    tmpar[i] = tmp.get(i);
                }
                result.add(tmpar);
            }
            increment(M, maxnumber - 1);
        }
        return result;
    }

    /**
     * Berechnet kombatible Blöcke
     *
     * @return
     */
    public ArrayList<ArrayList<byte[]>> findCompatibleBlogs() {
        int N = sums.length; // Anzahl der BlÃ¶cke
        ArrayList<ArrayList<byte[]>> posibleblogs = new ArrayList<ArrayList<byte[]>>();
        ArrayList<ArrayList<byte[]>> result = new ArrayList<ArrayList<byte[]>>();
        ArrayList<byte[]> zeile; // Zeile, bestehend aus sims.size BlÃ¶ckewn
        boolean[] used;
        int[] index;

        boolean zeileok;

        index = new int[N];

        result.clear();

        // Bestimme fuer jede Summe alle moeglichen bloecke
        for (int i = 0; i < N; ++i) {
            posibleblogs.add(possibleNumbers(sums[i]));
        }

        do {
            used = new boolean[maxnumber]; // wird mit false initialisaiert
            zeile = new ArrayList<byte[]>();
            zeileok = true;
            for (int i = 0; i < N; ++i) {
                for (int j = 0; j < posibleblogs.get(i).get(index[i]).length; ++j) {
                    byte anumber = posibleblogs.get(i).get(index[i])[j];
                    if (!used[anumber - 1]) {
                        used[anumber - 1] = true;
                        // zeile.push_back(anumber); // Wenn Zahl nochj nicht
                        // benutzt dranhÃ¤ngen (old version)
                    } else {
                        zeileok = false;
                        break; // ansonten test abrechen
                    }
                }
                if (!zeileok)
                    break; // auÃere schleife verlassen
                else
                    zeile.add(posibleblogs.get(i).get(index[i])); // Den Block
                // anhaengen
            }
            if (zeileok)
                result.add(zeile);
        } while (incrementindex(index, posibleblogs));
        return result;
    }

    public int determineNumberOfpermurtations() {
        int numberOfPermutations = 0;
        compatibleBlogs = findCompatibleBlogs();
        maxNumberOfPlaces = 0;

        for (int o = 0; o < compatibleBlogs.size(); ++o) {
            ArrayList<byte[]> tmp = compatibleBlogs.get(o);
            int tmpnumberOfPermutations = 1;
            for (int p = 0; p < tmp.size(); ++p) {
                tmpnumberOfPermutations *= LittleHelpers
                        .faculty(tmp.get(p).length);
            }
            int digits = 0;
            for (byte[] block : tmp) {
                digits += block.length;
            } // Anzahl der stellen bestimmen
            int freezeros = lineSize - digits - (tmp.size() - 1);
            if (freezeros < 0)
                continue; // dieser blog passt nicht in die Zeile
            int numberOfPlaces = tmp.size() + 1;

            if (maxNumberOfPlaces < numberOfPlaces)
                maxNumberOfPlaces = numberOfPlaces;

            int numberOfZerosPossibilities = LittleHelpers.faculty(freezeros
                    + numberOfPlaces - 1);
            numberOfZerosPossibilities /= LittleHelpers.faculty(freezeros)
                    * LittleHelpers.faculty(numberOfPlaces - 1);
            numberOfPermutations += tmpnumberOfPermutations
                    * numberOfZerosPossibilities;
        }
        this.numberOfPermutations = numberOfPermutations;
        return numberOfPermutations;
    }

    /**
     * Baue aus den kombatiblen Blöcken alle Permutationen pro Zeile/Spalte
     */
    public void calculateAllPermutyations() {
        zeilen = new LinkedList<byte[]>();
        int i = 0;
        int mnofz; // ((momentane anzahl von plätzen fuer freie nullen;

        if (compatibleBlogs == null) {
            numberOfPermutations = determineNumberOfpermurtations();
        }

        byte[][] alloczeilen = new byte[numberOfPermutations][lineSize]; // um den Speicher als ganzes zu allocieren und nicht fuer
        // jede Permutation einzeln- spart speicher und Zeit
        int[] zerosAtPlace = new int[maxNumberOfPlaces];

        int zeileToAdd = 0;
        for (ArrayList<byte[]> blogset : compatibleBlogs) {
            int digits = 0;
            for (byte[] block : blogset) {
                digits += block.length;
            } // Anzahl der stellen bestimmen
            int freezeros = lineSize - digits - (blogset.size() - 1);
            if (freezeros < 0)
                continue; // dieser blog passt nicht in die Zeile

            //int[] zerosAtPlace = new int[blogset.size() + 1];
            mnofz = blogset.size() + 1; // ((momentane anzahl von plätzen fuer freie nullen;
            for (int l = 0; l < maxNumberOfPlaces; ++l) zerosAtPlace[l] = 0;
            zerosAtPlace[mnofz - 1] = freezeros;


            boolean notcomplete = true;
            do {
                int j;

                do {
                    j = blogset.size() - 1;
                    int momplaceforzeros = 0;
                    //	byte[] zeile = new byte[lineSize];
                    byte[] zeile = alloczeilen[zeileToAdd];
                    for (i = 0; i < zerosAtPlace[momplaceforzeros]; ++i) {
                        zeile[i] = 0;
                    }
                    for (byte[] block : blogset) {
                        for (int k = 0; k < block.length; ++k) {
                            zeile[i++] = block[k];
                        }
                        ++momplaceforzeros;
                        for (int k = 0; k < zerosAtPlace[momplaceforzeros]; ++k) {
                            zeile[i++] = 0;
                        }
                        if (momplaceforzeros != mnofz - 1)
                            zeile[i++] = 0; // obligatorische Trennungsnull
                        // außer ganz hinten
                    }
                    i = 0;
                    zeilen.add(zeile);
                    ++zeileToAdd;
                    while (!LittleHelpers.next_permutation(blogset.get(j))) {
                        --j;
                        if (j < 0)
                            break;
                    }
                } while (j >= 0);

                boolean fertig;
                int geradezuinkrementieren = mnofz - 2; // vorletztes
                // Element
                do {
                    int obergrenze = freezeros;
                    int indexeins = 0;
                    for (indexeins = 0; indexeins != geradezuinkrementieren; ++indexeins) {
                        obergrenze -= zerosAtPlace[indexeins];
                    }
                    if (zerosAtPlace[geradezuinkrementieren] < obergrenze) {
                        ++zerosAtPlace[geradezuinkrementieren];
                        fertig = true;
                    } else {
                        if (indexeins != 0) {
                            zerosAtPlace[geradezuinkrementieren] = 0;
                            --geradezuinkrementieren;
                            fertig = false;
                        } else {
                            notcomplete = false;
                            fertig = true;
                            break;
                        }
                    }
                } while (!fertig);
                zerosAtPlace[mnofz - 1] = freezeros;
                for (int h = 0; h < (mnofz - 1); ++h) {
                    zerosAtPlace[mnofz - 1] -= zerosAtPlace[h];
                }
                // Die Elemente von Zerosatplace werden so durchiteriert, dass
                // fuer das 1. Element die Werte 0..freezeros durchlaufen werden
                // fuer das 2. Element die Werte 0..frezeros-1. Element
                // usw das letzte Glied ergibt sich aus den anderen, so das die
                // Summe
                // freezeros ist

            } while (notcomplete);
        }
        alloczeilen = null;
        if (zeilen.size() == 0) {
            throw new IllegalStateException("There is no solution!");
        }
        recentPermutation = zeilen.listIterator(); // listiterator auf erste permutation
        // * For debugging and testing:

        // for(byte[] zeile : zeilen) {
        // for(int k=0;k<zeile.length;++k) {
        // System.out.print(zeile[k]);
        // }
        // System.out.println("");
        // }
    }


}
