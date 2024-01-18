//TODO completer noms trinomes
/**
 * La classe LexVelo implemente un analyseur lexical pour une application de location de velos
 * A COMPLETER
 * 
 * @author ??, ??, ??
 * janvier 2024
 */

import java.io.InputStream;

public class LexVelo extends Lex {

	/** codage des items lexicaux */
	protected final int
		ADULTE = 0, DEBUT = 1, ENFANT = 2, FIN = 3, HEURES = 4, IDENT = 5, NBENTIER = 6, VIRG = 7,
		PTVIRG = 8, BARRE = 9, AUTRES = 10;
	/** tableau de l'affichage souhaite des items lexicaux */
	public static final String[] images = { "ADULTE", "DEBUT", "ENFANT", "FIN", "HEURES", "IDENT",
		"NBENT", "VIRG", "PTVIRG", "BARRE", "AUTRE" };

	/** nombre de mots reserves dans l'application Velo */
	private final int NBRES = 5 ;

	/** attributs lexicaux associes resp. a NBENTIER et IDENT */
	private int valEnt, numIdCourant;
	
	/** methodes d'acces aux attributs lexicaux */
	public int getvalEnt() {
		return this.valEnt;
	}
	public int getNumIdCourant() {
		return this.numIdCourant;
	}

	/** caractere courant */
	private char carLu ; 
	/** methode d'acces a l'attribut caractere courant */
	public char getCarLu(){
		return this.carLu;
	}
    
    /** constructeur classe LexVelo 
     * 
     * @param flot : donnee d'entree a analyser 
     * */
    public LexVelo(InputStream flot) {
	 	/** initialisation du flot par la classe abstraite */
    	super(flot);
    	
    	/** prelecture du premier caractere de la donnee */
    	lireCarLu();
    	
    	/** initialisation de tabIdent par mots reserves de l'application Velo */
    	this.tabIdent.add(0, "ADULTE"); this.tabIdent.add(1, "DEBUT") ; this.tabIdent.add(2, "ENFANT") ; 
    	this.tabIdent.add(3, "FIN") ; this.tabIdent.add(4, "HEURES") ;
    }

	/** methode de lecture d'un nouveau caractere courant carLu 
	 * a partir de la donnee en entree flot
	*/
    private void lireCarLu() {
		carLu = Lecture.lireChar(flot);
		this.notifyObservers(carLu);
		/** transformation de tous les caracteres de separation en espaces et forcage lettres en majuscules */
		if ((carLu == '\r') || (carLu == '\n') || (carLu == '\t'))
			carLu = ' ';
		if (Character.isWhitespace(carLu))
			carLu = ' ';
		else
			carLu = Character.toUpperCase(carLu);
	} 


	/** definition de la methode abstraite lireSymb de Lex 
	 * 
	 * 	@return code de l'item lexical detecte
	 * */
	public int lireSymb() {

		/** on "avale" espaces ou assimiles */
		while (carLu == ' ')
			lireCarLu();

		/** On lit le caractère actuel pour savoir quel branche choisir dans l'automate **/
		boolean isAlphabetic = true;
		String s = new String("");

		/** Le caractère lu est une barre **/
		if (carLu == '/')
		{
			lireCarLu();
			return BARRE;
		}

		/** Le caractère lu est un point-virgule **/
		else if (carLu == ';')
		{
			lireCarLu();
			return PTVIRG;
		}

		/** Le caractère lu est une virgule **/
		else if (carLu == ',')
		{
			lireCarLu();
			return VIRG;
		}

		/** Le caractère lu est une lettre **/
		else if (Character.isAlphabetic(carLu))
		{
			isAlphabetic = true;
			s += carLu;
		}

		/** Le caractère lu est un chiffre **/
		else if (Character.isDigit(carLu))
		{
			isAlphabetic = false;
			s += carLu;
		}

		/** Autres **/
		else
		{
			lireCarLu();
			return AUTRES;
		}

		/** Détecte si ident **/
		if (isAlphabetic) {

			/** Lit la chaîne de caractères et vérifie si ce sont toujours des lettres **/
			lireCarLu();
			while (Character.isAlphabetic(carLu))
			{
				s += carLu;
				lireCarLu();
			}
			if (Character.isDigit(carLu)) return AUTRES;

			/** On met la chaîne de caractères en majuscule **/
			s.toUpperCase();

			/** Traite la chaîne de caractères **/
			switch (s)
			{
				case "ADULTE":
					return ADULTE;
				case "DEBUT":
					return DEBUT;
				case "ENFANT":
					return ENFANT;
				case "FIN":
					return FIN;
				case "HEURES":
					return HEURES;
				default:
					/** Détecter le mot reconnu dans le tableau **/
					int i = IDENT;
					for (; i < tabIdent.size(); ++i)
					{
						if (s.equals(tabIdent.get(i)))
						{
							numIdCourant = i;
							break;
						}
					}

					/** Si pas reconnu, alors ajout **/
					if (i == tabIdent.size())
					{
						numIdCourant = i;
						tabIdent.add(i, s);
					}

					/** Fin **/
					return IDENT;
			}
		}

		/** Détecte si chiffre **/
		else {
			/** Lit la chaîne de caractères et vérifie si ce sont toujours des chiffres **/
			lireCarLu();
			carLu = getCarLu();
			while (Character.isDigit(carLu)) {
				s += carLu;
				lireCarLu();
			}
			if (Character.isAlphabetic(carLu)) return AUTRES;

			/** Ce sont des chiffres **/
			valEnt = Integer.valueOf(s);
			return NBENTIER;
		}
	}

	/** definition de la methode abstraite chaineIdent de Lex 
	 * 
	 * @param numIdent : numero d'un ident dans la table des idents
	 * @return chaine correspondant a numIdent
	 * */
	public String chaineIdent(int numIdent)
	{
		return tabIdent.get(numIdent);
	} 
	
	/** utilitaire de test de l'analyseur lexical seul (sans analyse syntaxique) */
	private void testeur_lexical() {
		/** Unite lexicale courante */
		int token;
		/** definition du caractere de fin de chaine 
		 * utile uniquement pour test autonome du lexical*/ 
		int finDeChaine = BARRE;
		do {
			token = lireSymb();
			if (token == NBENTIER)
				Lecture.attenteSurLecture("token : " + images[token] + " attribut valEnt = " + valEnt);
			else if (token == IDENT)
				Lecture.attenteSurLecture("token : " + images[token] + " attribut numIdCourant = " + numIdCourant
						+ " chaine associee = " + chaineIdent(numIdCourant));
			else
				Lecture.attenteSurLecture("token : " + images[token]);
		} while (token != finDeChaine);
	}

	
	/**
	 * Main pour tester l'analyseur lexical seul (sans analyse syntaxique)
	 */
	public static void main(String args[]) {
		
		String nomfich;
		nomfich = Lecture.lireString("nom du fichier d'entree : ");
		InputStream flot = Lecture.ouvrir(nomfich);
		if (flot == null) {
			System.exit(0);
		}

		LexVelo testVelo = new LexVelo(flot);
		testVelo.testeur_lexical();

		Lecture.fermer(flot);
		Lecture.attenteSurLecture("fin d'analyse");
		System.exit(0);

	}


}
