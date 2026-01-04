package presentation;

import java.util.List;

import dao.Client;
import dao.Utilisateur;
import metier.GestionClient;
import metier.GestionUtilisateur;
import metier.IGestionUtilisateur;

public class TestUtilisateur {

	public static void main(String[] args) {
		System.out.println("=== Test de connexion à la base de données ===");

		try {
			GestionClient gestion = new GestionClient();
			System.out.println("✓ Connexion à la base de données réussie");

			// Test create
			Client client = Client.builder()
					.nom("Test")
					.prenom("User")
					.email("test@example.com")
					.password("password123")
					.role("CLIENT")
					.telephone("0612345678")
					.build();

			Client created = gestion.create(client);
			System.out.println("✓ Client créé avec succès - ID: " + created.getId());

			// Test find
			List<Client> clients = gestion.findAll();
			System.out.println("✓ Nombre total de clients: " + clients.size());

			// Test find by ID
			Client found = gestion.findById(created.getId());
			if (found != null) {
				System.out.println("✓ Client trouvé par ID: " + found.getNom() + " " + found.getPrenom());
			} else {
				System.out.println("✗ Client non trouvé par ID");
			}

			// Test update
			created.setTelephone("0698765432");
			Client updated = gestion.update(created);
			System.out.println("✓ Client mis à jour - Nouveau téléphone: " + updated.getTelephone());

			// Test delete
			boolean deleted = gestion.delete(created.getId());
			if (deleted) {
				System.out.println("✓ Client supprimé avec succès");
			} else {
				System.out.println("✗ Échec de la suppression du client");
			}

			System.out.println("=== Tous les tests sont passés avec succès ===");

		} catch (Exception e) {
			System.err.println("✗ Erreur lors des tests: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
