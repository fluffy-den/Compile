import java.io.InputStream;
import java.util.ArrayList;

/**
 * La classe ActVelo met en oeuvre les actions de l'automate d'analyse syntaxique des locations de velos
 * 
 * @author NOE Louis-Quentin, MBASSI ATANGANA Blaise, POINT Marie
 * janvier 2024
 */


public class ActVelo extends AutoVelo {

	/** table des actions */
	private final int[][] ACTION =
		{ 	/* Etat     ADULTE DEBUT ENFANT   FIN   HEURES  IDENT  NBENTIER  VIRG PTVIRG  BARRE AUTRES  */
			/* 0  */    {  -1,  -1,    -1,    -1,     -1,     1,      -1,     -1,   -1,    10,    -1     },
			/* 1  */    {  -1,   4,    -1,     6,     -1,    -1,       2,     -1,   -1,    -1,    -1     },
			/* 2  */    {  -1,  -1,    -1,    -1,     -1,    -1,      -1,     -1,   -1,    -1,    -1     },
			/* 3  */    {  -1,   3,    -1,     5,     -1,    -1,      -1,     -1,   -1,    -1,    -1     },
			/* 4  */    {  -1,  -1,    -1,    -1,     -1,    -1,      -1,     -1,    9,    -1,    -1     },
			/* 5  */    {  -1,  -1,    -1,    -1,     -1,    -1,       2,     -1,   -1,    -1,    -1     },
			/* 6  */    {   7,  -1,     8,    -1,     -1,    -1,      -1,     -1,   -1,    -1,    -1     },
			/* 7  */    {  -1,  -1,    -1,    -1,     -1,    -1,       2,     -1,    9,    -1,    -1     },
			/* 8  */    {  -1,  -1,    -1,    -1,     -1,    -1,      -1,     -1,    9,    -1,    -1     },
			/* 9  */    {  -1,  -1,     8,    -1,     -1,    -1,      -1,     -1,   -1,    -1,    -1     },
			/* 10 */    {  -1,  -1,    -1,    -1,     -1,    -1,      -1,     -1,   -1,    -1,    -1     },
			/* 11 */ 	{  -1,  -1,    -1,    -1,     -1,    -1,      -1,     -1,   -1,    -1,    -1     }
		} ;

	/** constructeur classe ActVelo
	 * @param flot : donnee a analyser
	 *  */
	public ActVelo(InputStream flot) {
		super(flot);
	}
	

	/** definition de la methode abstraite getAction de Automate 
	 * 
	 * @param etat : code de l'etat courant
	 * @param unite : code de l'unite lexicale courante
	 * @return code de l'action suivante
	 **/
	public int getAction(int etat, int unite) {
		return ACTION[etat][unite];
	}

	/**
	 * definition methode abstraite initAction de Automate
	 */
	public void initAction() {
		/**  correspond a l'action 0 a effectuer a l'init */
		initialisations();
	}

	/** definition de la methode abstraite faireAction de Automate 
	 * 
	 * @param etat : code de l'etat courant
	 * @param unite : code de l'unite lexicale courante
	 * @return code de l'etat suivant
	 **/
	public void faireAction(int etat, int unite) {
		executer(ACTION[etat][unite]);
	}

	/** types d'erreurs detectees */
	private static final int FATALE = 0, NONFATALE = 1;
	
	/** gestion des erreurs 
	 * @param tErr type de l'erreur (FATALE ou NONFATALE)
	 * @param messErr message associe a l'erreur
	 */
	private void erreur(int tErr, String messErr) {
		Lecture.attenteSurLecture(messErr);
		switch (tErr) {
		case FATALE:
			errFatale = true;
			break;
		case NONFATALE:
			etatCourant = etatErreur;
			break;
		default:
			Lecture.attenteSurLecture("parametre incorrect pour erreur");
		}
	}

	/** attribut sauvegardant l'ensemble des locations en cours (non terminees) */
	private BaseDeLoc maBaseDeLoc = new BaseDeLoc();

	
	/** nombre de velos initialement disponibles */
	private static final int MAX_VELOS_ADULTES = 50, MAX_VELOS_ENFANTS = 20;
	

	/**
	 * acces a un attribut lexical 
	 * cast pour preciser que analyseurLexical est de type LexVelo
	 * @return valEnt associe a l'unite lexicale NBENTIER
	 */
	private int valEnt() {
		return ((LexVelo)analyseurLexical).getvalEnt();
	}
	/**
	 * acces a un attribut lexical 
	 * cast pour preciser que analyseurLexical est de type LexVelo
	 * @return numIdCourant associe a l'unite lexicale IDENT
	 */
	private int numId() {
		return ((LexVelo)analyseurLexical).getNumIdCourant();
	}


	
	/** variables 
	 * a prevoir pour actions */ 

	// Rappel: chaque <Validation> correspond a un jour different
	// jourCourant correspond a la <Validation> en cours d'analyse
	private int jourCourant=1;
	
	// Rappel: chaque <Validation> est composee de plusieurs operations 
	// nbOperationTotales correspond a toutes les operations contenues dans la donnee a analyser
	// erronees ou non
	private int nbOperationTotales;
	
	// nbOperationCorrectes correspond a toutes les operations sans erreur 
	// de la donnee a analyser
	private int nbOperationCorrectes;

	// nombre de vélos restants
	private int nbVelosAdultesRestants;
	private int nbVelosEnfantsRestants;

	// ensemble des clients differents vus chaque jour 
	// clientsParJour.get(i) donne l'ensemble des clients differents vus le ieme jour
	//		(NB: SmallSet.class fourni dans libClass_UtilitairesVelo)
	private ArrayList<SmallSet> clientsParJour;

	/**
	 * initialisations a effectuer avant les actions
	 */
	private void initialisations() {
		this.nbOperationCorrectes = 0; this.nbOperationTotales = 0;
		this.clientsParJour=new ArrayList<SmallSet>();
		/** initialisation clients du premier jour
		 * NB: le jour 0 n'est pas utilise */
		this.clientsParJour.add(0,new SmallSet());
		this.clientsParJour.add(1,new SmallSet());
		this.nbVelosAdultesRestants = MAX_VELOS_ADULTES;
		this.nbVelosEnfantsRestants = MAX_VELOS_ENFANTS;
	} // fin initialisations

	/**
	 * execution d'une action
	 * @param numAction :  numero de l'action a executer
	 */
	public void executer(int numAction) {
		System.out.println("etat  " + etatCourant + "  action  " + numAction);

		// getNumIdCourant = id du client actuel
		// getvalEnt = valeur entière de nbentier

		switch (numAction) {
			case -1:	// action vide
				break;

			case 0:
				// Réinitialize les variables des actions
				this.initAction();
				break;

			case 1:

				break;

			case 2:
				// Lis le nombre récupéré par l'analyseur lexical //TODO
				break;

			case 3:

				break;

			case 4:

				break;

			case 5:

				break;

			case 6:

				break;

			case 7:

				break;

			case 8:

				break;

			case 9:

				break;

			case 10:

				break;

			default:
				Lecture.attenteSurLecture("action " + numAction + " non prevue");
		}

		// Ajout d'une operation correcte
		this.nbOperationCorrectes += 1;
	} // fin executer


	/**
	 * 
	 * utilitaire de calcul de la duree d'une location
	 *
	 * @param jourDebutLoc : numero du jour de début de la location à partir de 1
	 * @param heureDebutLoc: heure du debut de la location, entre 8 et 19
	 * @param jourFinLoc   : numero du jour de la fin de la location à partir de 1
	 * @param heureFinLoc  : heure de fin de la location, entre 8 et 19
	 * @return nombre d'heures comptabilisées pour la location 
	 * 			(les heures de nuit entre 19h et 8h ne sont pas comptabilisees)
	 */
	private int calculDureeLoc(int jourDebutLoc, int heureDebutLoc, int jourFinLoc, int heureFinLoc) {
		int duree;
		// velos rendus le jour de l'emprunt
		if (jourDebutLoc == jourFinLoc) { 
			if (heureFinLoc != heureDebutLoc) duree = heureFinLoc - heureDebutLoc; 
			else duree = 1;
		// velos rendus quelques jours apres l'emprunt (la duree ne peut pas etre negative)
		} else { 
			duree = 19 - heureDebutLoc; // duree du premier jour
			duree = duree + (heureFinLoc - 8); // ajout de la duree du dernier jour
			if (jourFinLoc > jourDebutLoc + 1) { // plus 24h par jour intermediaire
				duree = duree + 11 * (jourFinLoc - jourDebutLoc - 1);
			} 
		}
		return duree;
	}


}
