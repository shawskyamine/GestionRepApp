package metier;

import java.util.List;

import dao.Client;
import exception.ClientNotFoundException;

public interface IGestionClient {
	public void ajouterClient(Client x);
	public Client rechercherClient(Long id) throws ClientNotFoundException;
	public void modifierClient(Client x);
	public void supprimerClient(Long id);
	public List<Client> listerClient();

}
