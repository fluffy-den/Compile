//package utilitaires;

import java.io.*;
import java.util.Scanner;

/**
 * quelques primitives de lecture au clavier ou dans un fichier 
 * @author Girard, Masson, Perraudeau
 *
 */
 

public class Lecture {

	/**
	 * delivre un pointeur sur le fichier de nom nomFich (null si erreur)
	 * 
	 * @param nomFich : nom du fichier d'entree
	 * @return flot correspondant aux donnees du fichier
	 */
	public static InputStream ouvrir(String nomFich) {
		InputStream f;
		try {
			f = new DataInputStream(new FileInputStream(nomFich));
		} catch (FileNotFoundException e) {
			System.out.println("fichier " + nomFich + " inexistant");
			f = null;
		}
		return f;
	}

	/**
	 * determine si la fin de fichier est atteinte
	 * 
	 * @param f : fichier contenant le flot de donnee a analyser
	 * @return 	vrai si fin de fichier trouvee
	 */
	public static boolean finFichier(InputStream f) {
		try {
			return (f != System.in && f.available() == 0);
		} catch (IOException e) {
			System.out.println("pb test finFichier");
			System.exit(1);
		}
		return true;
	}

	/**
	 * ferme un fichier (affiche un message si probleme)
	 * 
	 * @param f : fichier contenant le flot de donnee a analyser
	 */
	public static void fermer(InputStream f) {
		try {
			f.close();
		} catch (IOException e) {
			System.out.println("pb fermeture fichier");
		}
	}

	/**
	 * lecture d'un octet dans la chaine d'entree (avec capture de l'exception)
	 * 
	 * @param f : fichier contenant le flot de donnee a analyser
	 * @return 	caractere courant lu dans f
	 */
	public static char lireChar(InputStream f) {
		char carSuiv = ' ';
		try {
			int x = f.read();
			if (x == -1) {
				System.out.println("lecture apres fin de fichier");
				System.exit(2);
			}
			carSuiv = (char) x;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Erreur fatale");
			System.exit(3);
		}
		return carSuiv;
	}

	/**
	 * lecture d'un octet dans la chaine d'entree au clavier
	 * 
	 * @return caractere courant lu
	 */
	public static char lireChar() {
		return lireChar(System.in);
	}

	/**
	 * lecture d'une chaine au clavier suivie de l'affichage d'un message
	 * 
	 * @param mess : message à afficher (après lecture chaine)
	 * @return 	chaine lue
	 **/
	public static String lireString(String mess) {
		Scanner entree = new Scanner(System.in);
		System.out.println("");
		System.out.print(mess);
		String temp = entree.nextLine();
		return temp;
	}

	/**
	 * affichage d'un message a l'ecran et attente jusque retour-a-la-ligne fourni
	 * 
	 * @param mess : message à afficher
	 **/
	public static void attenteSurLecture(String mess) {
		Scanner sc = new Scanner(System.in);
		System.out.println("");
		System.out.print(mess + " pour continuer tapez entree ");
		String tempo = sc.nextLine();
	}

	/** programme principal ne servant qu'au test des utilitaires */
	public static void main(String args[]) {
		InputStream fichier;
		char carLu;
		fichier = ouvrir("test");
		while (!finFichier(fichier)) {
			carLu = lireChar(fichier);
			System.out.print(carLu);
		}
		fermer(fichier);

		// lecture au clavier
		carLu = lireChar();
	}
}
