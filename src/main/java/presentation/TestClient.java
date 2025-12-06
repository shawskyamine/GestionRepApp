package presentation;

import dao.Client;
import exception.ClientNotFoundException;
import metier.GestionClient;
import metier.IGestionClient;

public class TestClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IGestionClient igc= new GestionClient();
		Client c1=Client.builder()
				.nom("Souleimani")
				.prenom("Rayan")
				.numTel("060000000").build();
		Client c2=Client.builder()
				.nom("Souleiman")
				.prenom("Ali")
				.numTel("060000000").build();
		igc.ajouterClient(c2);
		igc.ajouterClient(c1);
		c2.setNumTel("0611111111");
		c2.setNom("a");
       igc.modifierClient(c2);
       try {
		System.out.println(igc.rechercherClient(2L));
		igc.supprimerClient(2L);
	} catch (ClientNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       igc.listerClient().stream()
       .forEach(System.out::println);
       
	}

}
