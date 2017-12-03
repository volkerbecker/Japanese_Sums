package de.amazingsax.japanese_sums;
/**
 * some gerenral helping methods
 * @author becker
 *
 */
public class LittleHelpers {
	
	private LittleHelpers() {}

	/**
	 * Erzeugt die nächstgrößere Permutation, größer bezieht sich
	 * auf Lexikalische Ordnung. (kleinstes Element sortiert in aussteigender
	 * Reihenfolge), enstspricht nexpermutation aus der C++ STL
	 *
	 * @param p Referenz auf aktuelle Permutation
	 *
	 * @return false wenn alles durchpermutiert wurde, sonst true
	 *
	 */
	public static boolean next_permutation(byte[] p) {
		  for (int a = p.length - 2; a >= 0; --a)
		    if (p[a] < p[a + 1])
		      for (int b = p.length - 1;; --b)
		        if (p[b] > p[a]) {
		          byte t = p[a];
		          p[a] = p[b];
		          p[b] = t;
		          for (++a, b = p.length - 1; a < b; ++a, --b) {
		            t = p[a];
		            p[a] = p[b];
		            p[b] = t;
		          }
		          return true;
		        }
		  //wenn keine permutation uebrig liste sortieren
		  java.util.Arrays.sort( p );
		  return false;
		}

	/**
	 * Erzeugt Zufallszahl zwischen 0 und max
	 * @param max
	 * @return Zufallszahl zwischen 0..max
	 */
	public static byte myrandom(byte max) {
		++max;
		return (byte) (Math.random()*max);
	}

	/**
	 * Berechnet die Fakultät der Zahl (schneller als rekusive Berechnung)
	 * @param zahl Zahl N
	 * @return N!
	 */
	public static int faculty(int zahl) {
		int result=1;
		for(int i=1;i<=zahl;++i) {
			result*=i;
		}
		return result;
	}
}
