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
	{       /* Etat     ADULTE DEBUT ENFANT   FIN   HEURES  IDENT  NBENTIER  VIRG PTVIRG  BARRE AUTRES  */
			/* 0  */   {  10,   10,    10,     10,    10,     1,      10,    10,   10,      9,    10   },
			/* 1  */   {  10,    3,    10,      5,    10,    10,      10,    10,   10,     10,    10   },
			/* 2  */   {  10,   10,    10,     10,    10,    10,      10,    10,   10,     10,    10   },
			/* 3  */   {  10,    2,    10,      4,    10,    10,      10,    10,   10,     10,    10   },
			/* 4  */   {  10,   10,    10,     10,    10,    10,      10,    10,   10,     10,    10   },
			/* 5  */   {  10,   10,    10,     10,    10,    10,      10,    10,   10,     10,    10   },
			/* 6  */   {   6,   10,     7,     10,    10,    10,      10,    10,   10,     10,    10   },
			/* 7  */   {  10,   10,    10,     10,    10,    10,      10,    10,    8,     10,    10   },
			/* 8  */   {  10,   10,     7,     10,    10,    10,      10,    10,   10,     10,    10   },
			/* 9  */   {  10,   10,    10,     10,    10,     1,      10,    10,   10,     10,    10   },
			/* 10 */   {  -1,   -1,    -1,     -1,    -1,    -1,      -1,    -1,   -1,     11,    -1   },
			/* 11 */   {  -1,   -1,    -1,     -1,    -1,    -1,      -1,    -1,   -1,     -1,    -1   }
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
			Lecture.attenteSurLecture("Parametre incorrect pour erreur");
		}
	}

	/** attribut sauvegardant l'ensemble des locations en cours (non terminees) */
	private BaseDeLoc maBaseDeLoc = new BaseDeLoc();

	
	/** nombre de velos initialement disponibles */
	private static final int MAX_VELOS_ADULTES = 50;
	private static final int MAX_VELOS_ENFANTS = 20;

	/** tarifs des velos **/
	private static final int TARIF_ADULTES = 4;
	private static final int TARIF_ENFANTS = 2;
	

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
	private int jourCourant;
	
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
		this.jourCourant=1;
		this.clientsParJour=new ArrayList<SmallSet>();
		/** initialisation clients du premier jour
		 * NB: le jour 0 n'est pas utilise */
		this.clientsParJour.add(0,new SmallSet());
		this.clientsParJour.add(1,new SmallSet());
		this.nbVelosAdultesRestants = MAX_VELOS_ADULTES;
		this.nbVelosEnfantsRestants = MAX_VELOS_ENFANTS;
	} // fin initialisations

	/**
	 * @return Cast de this.analyseurLexical vers le type fille de type LexVelo
	 */
	private LexVelo getLex() {
		return (LexVelo)this.analyseurLexical;
	}

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

	/**
	 * execution d'une action
	 * @param numAction :  numero de l'action a executer
	 */
	public void executer(int numAction) {
		System.out.println("etat  " + etatCourant + "  action  " + numAction);

		// Ceci est une nouvelle opération
		this.nbOperationTotales++;

		// Récupère l'instance de l'analyseur lexical
		LexVelo lex = this.getLex();

		// getNumIdCourant = id du client actuel
		// getvalEnt = valeur entière de nbentier
		switch (numAction) {
			case -1: {    // action vide
				break;
			}

			case 0: {
				// Réinitialize les variables des actions
				this.initAction();
				break;
			}

			case 1: {
				// Gère un nouveau identifiant
				String nom = lex.chaineIdent(lex.getNumIdCourant());

				// Ajout d'un nouveau client
				BaseDeLoc.InfosClient infos = maBaseDeLoc.getInfosClient(nom);
				if (infos == null) {
					maBaseDeLoc.enregistrerLoc(nom, this.jourCourant, -1, -1, -1);
				}
				else
				{
					erreur(NONFATALE, "Erreur - Le client " + nom + " est déjà présent dans la liste.");
				}

				// Ajout du client dans la liste des clients du jour
				this.clientsParJour.get(this.jourCourant).add(lex.getNumIdCourant());
				break;
			}

			case 2: {
				// Récupération de la base donnée du client
				BaseDeLoc.InfosClient infos = maBaseDeLoc.getInfosClient(lex.chaineIdent(lex.getNumIdCourant()));
				if (infos == null) {
					erreur(NONFATALE, "Erreur - L'identifiant : " + lex.getNumIdCourant() + " n'a pas de client associé.");
					return;
				}

				// Récupération de l'heure de début
				int hdebut = lex.getvalEnt();
				if (hdebut < 8 || hdebut > 19)
				{
					erreur(NONFATALE, "Erreur - La valeur de l'heure de début : " + hdebut + " n'est pas comprise dans [8, 19].");
					return;
				}

				// Modification heures debut du client actuel
				infos.heureDebut = hdebut;
				break;
			}

			case 3: {
				// Récupération de la base donnée du client
				BaseDeLoc.InfosClient infos = maBaseDeLoc.getInfosClient(lex.chaineIdent(lex.getNumIdCourant()));
				if (infos == null) {
					erreur(NONFATALE, "Erreur - L'identifiant : " + lex.getNumIdCourant() + " n'a pas de client associé.");
					return;
				}

				// Modification heures debut du client actuel à 8 (par défaut)
				infos.heureDebut = 8;
				break;
			}

			case 4: {
				// Récupération de la base donnée du client
				BaseDeLoc.InfosClient infos = maBaseDeLoc.getInfosClient(lex.chaineIdent(lex.getNumIdCourant()));
				if (infos == null) {
					erreur(NONFATALE, "Erreur - L'identifiant : " + lex.getNumIdCourant() + " n'a pas de client associé.");
					return;
				}

				// Récupération de l'heure de fin
				int hfin = lex.getvalEnt();
				if (hfin < 8 || hfin > 19)
				{
					erreur(NONFATALE, "Erreur - La valeur de l'heure de fin : " + hfin + " n'est pas comprise dans [8, 19].");
					return;
				}
				if (hfin < infos.heureDebut)
				{
					erreur(FATALE, "Erreur Fatale - L'heure de fin : " + hfin + " est plus petite que l'heure de début : " + infos.heureDebut);
					return;
				}

				// Suppression de la location en cours
				maBaseDeLoc.supprimerClient(lex.chaineIdent(lex.getNumIdCourant()));

				// Récupération des vélos
				this.nbVelosAdultesRestants += infos.qteAdulte;
				this.nbVelosEnfantsRestants += infos.qteEnfant;

				// Calcul du montant à calculer
				int prix = calculDureeLoc(infos.jourEmprunt, infos.heureDebut, this.jourCourant, hfin) *
						(infos.qteAdulte * TARIF_ADULTES + infos.qteEnfant * TARIF_ENFANTS);

				// Affichage de la recette actuelle
				Ecriture.ecrireStringln("Le client: " + infos.nomClient + " doit payer : " + prix + " euros pour " +
						infos.qteAdulte + " velo(s) adulte et " + infos.qteEnfant + " velo(s) enfant.");

				// Fin
				break;
			}

			case 5: {
				// Récupération de la base donnée du client
				BaseDeLoc.InfosClient infos = maBaseDeLoc.getInfosClient(lex.chaineIdent(lex.getNumIdCourant()));
				if (infos == null) {
					erreur(NONFATALE, "Erreur - L'identifiant : " + lex.getNumIdCourant() + " n'a pas de client associé.");
					return;
				}

				// Heure de fin par défaut
				int hfin = 19;

				// Calcul du montant à calculer
				int prix = calculDureeLoc(infos.jourEmprunt, infos.heureDebut, this.jourCourant, hfin) *
						(infos.qteAdulte * TARIF_ADULTES + infos.qteEnfant * TARIF_ENFANTS);

				// Suppression de la location en cours
				maBaseDeLoc.supprimerClient(lex.chaineIdent(lex.getNumIdCourant()));

				// Récupération des vélos
				this.nbVelosAdultesRestants += infos.qteAdulte;
				this.nbVelosEnfantsRestants += infos.qteEnfant;

				// Affichage de la recette actuelle
				Ecriture.ecrireStringln("Le client: " + infos.nomClient + " doit payer : " + prix + " euros pour " +
						infos.qteAdulte + " velo(s) adulte et " + infos.qteEnfant + " velo(s) enfant.");

				// Fin
				break;
			}

			case 6: {
				// Récupère la base donnée du client
				BaseDeLoc.InfosClient infos = maBaseDeLoc.getInfosClient(lex.chaineIdent(lex.getNumIdCourant()));
				if (infos == null) {
					erreur(NONFATALE, "Erreur - L'identifiant : " + lex.getNumIdCourant() + " n'a pas de client associé.");
					return;
				}

				// Récupère le nombre de vélos à louer pour les enfants
				int nbVelos = lex.getvalEnt();
				if (nbVelos > nbVelosEnfantsRestants)
				{
					erreur(NONFATALE, "Erreur - Tentative de location de vélos enfants plus grand que le nombre " +
							"disponible, nombre disponible : " + nbVelosEnfantsRestants + ", nombre demandé : " + nbVelos);
					return;
				}

				// Modification nombre d'enfants
				infos.qteEnfant = lex.getvalEnt();

				// Enlève les vélos en location
				nbVelosEnfantsRestants -= lex.getvalEnt();
				break;
			}

			case 7: {
				// Récupère la base donnée du client
				BaseDeLoc.InfosClient infos = maBaseDeLoc.getInfosClient(lex.chaineIdent(lex.getNumIdCourant()));
				if (infos == null) {
					erreur(NONFATALE, "Erreur - L'identifiant : " + lex.getNumIdCourant() + " n'a pas de client associé.");
					return;
				}

				// Récupère le nombre de vélos à louer pour les enfants
				int nbVelos = lex.getvalEnt();
				if (nbVelos > nbVelosAdultesRestants)
				{
					erreur(NONFATALE, "Erreur - Tentative de location de vélos adultes plus grand que le nombre " +
							"disponible, nombre disponible : " + nbVelosAdultesRestants + ", nombre demandé : " + nbVelos);
					return;
				}

				// Modification nb enfants
				infos.qteAdulte = lex.getvalEnt();

				// Enlève les vélos en location
				nbVelosAdultesRestants -= lex.getvalEnt();
				break;
			}

			case 8: {
				// Affichage du bilan du jour
				Ecriture.ecrireStringln("Bilan du jour " + this.jourCourant);
				Ecriture.ecrireStringln("Nombre de velos adulte manquants : " + (MAX_VELOS_ADULTES - nbVelosAdultesRestants));
				Ecriture.ecrireStringln("Nombre de velos enfants manquants : " + (MAX_VELOS_ENFANTS - nbVelosEnfantsRestants));
				Ecriture.ecrireStringln("Operations correctes : " + nbOperationCorrectes + " - Nombre total d'operations : " + nbOperationTotales);
				Ecriture.ecrireStringln("Voici les clients qui doivent encore rendre des velos");
				maBaseDeLoc.afficherLocationsEnCours();

				// Journée suivante
				this.jourCourant++;

				// Construction de la liste de clients du jour suivant
				this.clientsParJour.add(this.jourCourant, new SmallSet());
				break;
			}

			case 9: {
				// Calcul du jour avec le nombre le plus élevé de clients
				int maxJour = 1, maxClients = 0;
				for (int j = 1, t = this.clientsParJour.size(); j < t; ++j)
				{
					if (this.clientsParJour.get(j).size() > maxClients)
					{
						maxClients = this.clientsParJour.get(j).size();
						maxJour    = j;
					}
				}

				// Affichage
				Ecriture.ecrireStringln("Le jour de plus grande affluence est le jour : " + maxJour + " avec " +
						maxClients + " clients différents servis.");
				break;
			}

			case 10: {
				// Gestion erreur de syntaxe
				Ecriture.ecrireStringln("Erreur de syntaxe sur " + lex.getCarLu() + "...");
				break;
			}

			case 11: {
				// On ignore le dernier jour pour le bilan général
				this.clientsParJour.remove(this.jourCourant);
				this.jourCourant--;
				break;
			}

			default: {
				Lecture.attenteSurLecture("action " + numAction + " non prevue");
			}
		}

		// Si on arrive ici, l'opération est correcte et il n'y a pas eu d'erreurs
		this.nbOperationCorrectes++;
	} // fin executer


}
