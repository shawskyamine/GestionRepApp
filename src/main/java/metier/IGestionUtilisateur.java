package metier;

import dao.Utilisateur;
import exception.ConnectionFailedException;

public interface IGestionUtilisateur {

	
	public Utilisateur seConnecter(String identifiant , String mdp )throws ConnectionFailedException;
	public boolean seDeconnecter(Utilisateur u);
	

}
