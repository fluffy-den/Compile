import java.io.InputStream;

/**
 * La classe Velo correspond au programme principal d'un
 * analyseur syntaxique par automate d'etats fini avec actions
 * pour l'application Velo
 * 
 * @author Girard, Masson, Perraudeau
 * janvier 2022
 *
 */

public class Velo {

	/**
	 * Initialisation du fichier d'entree
	 * 
	 * @return flux de données provenant d'un fichier
	 */
	public static InputStream debutAnalyse() {
		String nomfich;
		nomfich = Lecture.lireString("nom du fichier d'entree : ");
		InputStream f = Lecture.ouvrir(nomfich);
		if (f == null) {
			System.exit(0);
		}
		return f;
	} 

	/**
	 * fermeture fichier d'entree et fenetre de trace d'execution
	 * 
	 * @param f : fichier d'entree
	 */
	public static void finAnalyse(InputStream f) {
		Lecture.fermer(f);
		/** fermeture de la fenetre de trace d'execution */
		char tempo ;
		System.out.println("");
		System.out.println(" pour fermer la fenetre de trace d'execution, tapez entree ") ;
		tempo = Lecture.lireChar() ;
		tempo = Lecture.lireChar() ;
		System.exit(0);
	} 
   
	/**
	 * Analyse syntaxique par automate d'etats finis avec actions,
	 * mis en oeuvre par interpreteur de tables
	 * 
	 * @param args ici supposé vide
	 */
	public static void main(String[] args) {

		FenAffichage fenetre = new FenAffichage();
		ActVelo analyseur;
		
		/** la donnee a analyser est initialisee dans la variable flot */
		InputStream flot = debutAnalyse();
		
		analyseur = new ActVelo(flot);
		analyseur.newObserver(fenetre, fenetre);
		analyseur.interpreteur();
		finAnalyse(flot);
	}
}

