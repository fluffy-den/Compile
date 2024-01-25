import java.io.*;

/**
 * La classe AutoVelo met en oeuvre l'automate d'analyse syntaxique des locations de velos
 * Realisation par interpreteur de tables
 * A COMPLETER
 * 
 * @author NOE Louis-Quentin, MBASSI ATANGANA Blaise, POINT Marie
 * janvier 2024
 */

public class AutoVelo extends Automate{
	
	/**
	 * RAPPEL: reprise après erreur demandee sur les items lexicaux VIRG, PTVIRG et BARRE
	 */

	/** table des transitions */

	private static final int[][] TRANSIT =
	{     /* Etat     ADULTE DEBUT ENFANT   FIN   HEURES  IDENT  NBENTIER  VIRG PTVIRG  BARRE AUTRES  */
			/* 0  */ {   0,    0,     0,      0,     0,     1,       0,     0,    0,    11,     0   },
			/* 1  */ {  10,    5,    10,      4,    10,    10,      10,    10,   10,    10,    10   },
			/* 2  */ {  10,   10,    10,     10,     3,    10,      10,    10,   10,    10,    10   },
			/* 3  */ {  10,    5,    10,      4,    10,    10,      10,    10,   10,    10,    10   },
			/* 4  */ {  10,   10,    10,     10,    10,    10,      10,     9,   10,    10,    10   },
			/* 5  */ {  10,   10,    10,     10,    10,    10,       6,    10,   10,    10,    10   },
			/* 6  */ {   7,   10,     4,     10,    10,    10,      10,    10,   10,    10,    10   },
			/* 7  */ {  10,   10,    10,     10,    10,    10,       8,     9,    0,    10,    10   },
			/* 8  */ {  10,   10,     4,     10,    10,    10,      10,    10,   10,    10,    10   },
			/* 9  */ {  10,   10,    10,     10,    10,     1,      10,    10,   10,    10,    10   },
			/* 10 */ {  -1,   -1,    -1,     -1,    -1,    -1,      -1,     1,    1,    11,    -1   },
			/* 11 */ {  -1,   -1,    -1,     -1,    -1,    -1,      -1,    -1,   -1,    -1,    -1   }
	};

	/** gestion de l'affichage sur la fenetre de trace de l'execution */
	public void newObserver(ObserverAutomate oAuto, ObserverLexique oLex ){
		this.newObserver(oAuto);
		this.analyseurLexical.newObserver(oLex);
		analyseurLexical.notifyObservers(((LexVelo)analyseurLexical).getCarLu());
	}
	/** fin gestion de l'affichage sur la fenetre de trace de l'execution */

	/**
	 *  constructeur classe AutoVelo pour l'application Velo
	 *  
	 * @param flot : donnee a analyser
	 */
	public AutoVelo(InputStream flot) {
		/** on utilise ici un analyseur lexical de type LexVelo */
		analyseurLexical = new LexVelo(flot);
		/** initialisation etats particuliers de l'automate fini d'analyse syntaxique*/
		this.etatInitial = 0;
		this.etatFinal = TRANSIT.length;
		this.etatErreur = TRANSIT.length - 1;
	}

	/** definition de la methode abstraite getTransition de Automate 
	 * 
	 * @param etat : code de l'etat courant
	 * @param unite : code de l'unite lexicale courante
	 * @return code de l'etat suivant
	 **/
	int getTransition(int etat, int unite) {
		return this.TRANSIT[etat][unite];
	}

	/** ici la methode abstraite faireAction de Automate n'est pas encore definie */
	void faireAction(int etat, int unite) {};

	/** ici la methode abstraite initAction de Automate n'est pas encore definie */
	void initAction() {};

	/** ici la methode abstraite getAction de Automate n'est pas encore definie */
	int getAction(int etat, int unite) {return 0;};

}
