import java.util.ArrayList;

/**
 * classe BaseDeLoc representant les locations non terminees
 * 
 * @author Girard, Grazon, Masson
 * juillet 2019
 *
 */

// Exemple de Base de Loc en fin de traitement (18 velos non rendus)
//		CLIENT  JOUR	HEURE_DEBUT    ADULTE    ENFANT
//---------                   --        --       ---     
//      GIRARD     1   		 8         3         1
//      GRAZON     3   		15         8         0
//      MASSON     3   		15         6         0

public class BaseDeLoc {
	
	/** largeur des colonnes pour affichage à l'écran */
	private static final int LARGEUR_COLONNE=10;
	
	/** 
	 * classe interne InfosClient 
	 * definissant les informations conservees pour une location non terminee
	 * @author Girard, Grazon, Masson
	 */
	public class InfosClient { 
		
		/** nom du client ayant loue des velos */
		public String nomClient;
		/** numero du jour de la location 
		 * 	heure de debut de la location (entre 8 et 19)
		 * 	nombres de velos adultes et enfants loues */
		public int jourEmprunt, heureDebut, qteAdulte, qteEnfant ;
		
		/** constructeur classe interne InfosClient */
		public InfosClient(String nom, int jour, int heurD, int qteA, int qteE) {
			this.nomClient = nom;
			this.jourEmprunt = jour ; 
			this.heureDebut = heurD ; 
			this.qteAdulte = qteA ; 
			this.qteEnfant = qteE ; 
		}
		
		/** méthode d'affichage des informations concernant un client pour une location en cours */
		public void afficheClient() {
			Ecriture.ecrireString(chaineCadrageGauche(this.nomClient,LARGEUR_COLONNE));
			Ecriture.ecrireInt(this.jourEmprunt, LARGEUR_COLONNE); 
			Ecriture.ecrireInt(this.heureDebut, LARGEUR_COLONNE);
			Ecriture.ecrireInt(this.qteAdulte, LARGEUR_COLONNE);
			Ecriture.ecrireInt(this.qteEnfant, LARGEUR_COLONNE); 
			Ecriture.ecrireStringln("");	    
		} 
		
	} // fin class InfosClient

	/** tableau de toutes les locations en cours  */
	private ArrayList<InfosClient> tableFiches ;

	/** constructeur classe BasedeLoc */
	public BaseDeLoc() {
		this.tableFiches = new ArrayList<InfosClient>();
	}

	
	/**
	 * methode qui enregistre une nouvelle location (en cours) dans BaseDeLoc
	 * 
	 * @param nomC : nom du client commencant une location 
	 * @param jour : jour de debut de la nouvelle location
	 * @param heurD: heure de debut de la nouvelle location
	 * @param qteA : quantite de velos adulte loues pour cette nouvelle location
	 * @param qteE : quantite de velos adulte loues pour cette nouvelle location
	 * 
	 * @precondition le client n'est pas deja present dans la base de location
	 */
	public void enregistrerLoc (String nomC, int jour, int heurD, int qteA, int qteE) { 
		InfosClient nouveauClient = new InfosClient (nomC, jour, heurD, qteA, qteE);
		tableFiches.add(nouveauClient);
	}
	
	/**
	 * methode d'acces aux information de la location en cours pour un client
	 * 
	 * @param nomClient : nom du client dont on cherche les informations
	 * @return les informations de la location en cours pour le client nomClient
	 * 			ou null si client inexistant
	 */
	public InfosClient getInfosClient(String nomClient) { 
		for(InfosClient client:tableFiches) {
			if(client.nomClient.equals(nomClient)) return client;
		}
		return null;
	}
	
	/**
	 * methode supprimant de BaseDeLoc une location en cours
	 * 
	 * @param nomClient : nom du client dont on veut supprimer la location de BaseDeLoc
	 * @precondition : le client est present dans BaseDeLoc
	 */
	public void supprimerClient(String nomClient) { 
		InfosClient clientRecherche = null;
		int i = 0;
		while(i< tableFiches.size() && clientRecherche == null) {
			if(tableFiches.get(i).nomClient.equals(nomClient)) { 
				clientRecherche =  tableFiches.get(i);
			}
			else i++;
		}
		if(clientRecherche != null) tableFiches.remove(clientRecherche);
	}
	
	/**
	 * utilitaire pour l'affichage a l'ecran en colonnes
	 * 
	 * @param uneChaine : chaine de longueur quelconque
	 * @param taille : taille de la colonne d'affichage
	 * @return uneChaine cadree a gauche sur taille caracteres
	 */
	private String chaineCadrageGauche(String uneChaine, int taille)
	{
		int longueurChaine = Math.min(LARGEUR_COLONNE, uneChaine.length());
		String chaineCadree = uneChaine.substring(0, longueurChaine);
		for (int k = longueurChaine; k < taille; k++)
			chaineCadree = chaineCadree + " ";
		return chaineCadree;
	}
	
	/** methode d'affichage de toutes les locations de BaseDeLoc */
	public void afficherLocationsEnCours() {
		Ecriture.ecrireStringln("");
		Ecriture.ecrireStringln("      CLIENT      JOUR    HEURE_DEBUT  ADULTE    ENFANT\n"
				              + "---------------    ----      ---       ---       ---     ");	
		for(InfosClient client: tableFiches) { 
			client.afficheClient();
		}	
	}

}

    