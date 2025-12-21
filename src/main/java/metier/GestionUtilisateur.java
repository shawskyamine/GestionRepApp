package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import dao.Utilisateur;
import exception.ConnectionFailedException;


public class GestionUtilisateur implements IGestionUtilisateur {

	private EntityManager em;
	public GestionUtilisateur() {
		// TODO Auto-generated constructor stub
		EntityManagerFactory emf= Persistence.createEntityManagerFactory("ClientUP");
		em= emf.createEntityManager();
	}
	
	@Override
	public Utilisateur seConnecter(String identifiant, String mdp) throws ConnectionFailedException {
		// TODO Auto-generated method stub
		try {
			Utilisateur u  = em.find(null, identifiant);
			if(u != null && u.getMdp().equals(mdp)) {
				return u;
			}
			
		}catch(Exception e) {
			throw new ConnectionFailedException("connection failed");
		}
		return null; //ila makhdmatch l connection z3ma les donnes mchi homa hadok anafichiw chi 7aja fl frontend
		
		
	}

	@Override
	public boolean seDeconnecter(Utilisateur u) {
		
		
		//andiro chi l3iba cote frontend , wla ila khdmna b sessions ghadi n invalidiw chi session 
		
		return false;
	}

	
	
}






































//public class GestionUtilisateur implements IGestionUtilisateur {
//
//	private EntityManager em;
//	public GestionUtilisateur() {
//		// TODO Auto-generated constructor stub
//		EntityManagerFactory emf= Persistence.createEntityManagerFactory("ClientUP");
//		em= emf.createEntityManager();
//	}
//	@Override
//	public void ajouterClient(Client x) {
//		// TODO Auto-generated method stub
//	
//		EntityTransaction tr= em.getTransaction();
//		try {
//			tr.begin();
//			em.persist(x);
//			tr.commit();
//			
//		}
//		catch(Exception e) {
//			tr.rollback();
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public Client rechercherClient(Long id) throws ClientNotFoundException {
//		// TODO Auto-generated method stub
//		Client x=em.find(Client.class, id);
//		if(x!=null) 
//			return x;
//		else 
//			throw new ClientNotFoundException("Client introuvable");
//	}
//
//	@Override
//	public void modifierClient( Client x) {
//		// TODO Auto-generated method stub
//		EntityTransaction tr= em.getTransaction();
//		try {
//			tr.begin();
//			em.merge(x);
//			tr.commit();
//			
//		}
//		catch(Exception e) {
//			tr.rollback();
//			e.printStackTrace();
//		}
//		
//	}
//
//	@Override
//	public void supprimerClient(Long id) {
//		// TODO Auto-generated method stub
//		EntityTransaction tr= em.getTransaction();
//		try {
//			tr.begin();
//			em.remove(rechercherClient(id));
//			tr.commit();
//			
//		}
//		catch(Exception e) {
//			tr.rollback();
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public List<Client> listerClient() {
//		// TODO Auto-generated method stub
//		Query req= em.createQuery("select c from Client c");
//		return req.getResultList();
//	}
//
//}



