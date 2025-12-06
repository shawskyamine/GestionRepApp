package metier;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import dao.Client;
import exception.ClientNotFoundException;

public class GestionClient implements IGestionClient {

	private EntityManager em;
	public GestionClient() {
		// TODO Auto-generated constructor stub
		EntityManagerFactory emf= Persistence.createEntityManagerFactory("ClientUP");
		em= emf.createEntityManager();
	}
	@Override
	public void ajouterClient(Client x) {
		// TODO Auto-generated method stub
	
		EntityTransaction tr= em.getTransaction();
		try {
			tr.begin();
			em.persist(x);
			tr.commit();
			
		}
		catch(Exception e) {
			tr.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public Client rechercherClient(Long id) throws ClientNotFoundException {
		// TODO Auto-generated method stub
		Client x=em.find(Client.class, id);
		if(x!=null) 
			return x;
		else 
			throw new ClientNotFoundException("Client introuvable");
	}

	@Override
	public void modifierClient( Client x) {
		// TODO Auto-generated method stub
		EntityTransaction tr= em.getTransaction();
		try {
			tr.begin();
			em.merge(x);
			tr.commit();
			
		}
		catch(Exception e) {
			tr.rollback();
			e.printStackTrace();
		}
		
	}

	@Override
	public void supprimerClient(Long id) {
		// TODO Auto-generated method stub
		EntityTransaction tr= em.getTransaction();
		try {
			tr.begin();
			em.remove(rechercherClient(id));
			tr.commit();
			
		}
		catch(Exception e) {
			tr.rollback();
			e.printStackTrace();
		}
	}

	@Override
	public List<Client> listerClient() {
		// TODO Auto-generated method stub
		Query req= em.createQuery("select c from Client c");
		return req.getResultList();
	}

}
