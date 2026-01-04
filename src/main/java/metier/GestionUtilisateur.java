package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import dao.Utilisateur;

public class GestionUtilisateur implements IGestionUtilisateur {

    private EntityManager em;

    public GestionUtilisateur() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public Utilisateur create(Utilisateur utilisateur) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(utilisateur);
            tr.commit();
            return utilisateur;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Utilisateur findById(int id) {
        try {
            Utilisateur u = em.find(Utilisateur.class, id);
            return u;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        try {
            TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Utilisateur findByNomAndPrenom(String nom, String prenom) {
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.nom = :nom AND u.prenom = :prenom",
                    Utilisateur.class);
            query.setParameter("nom", nom);
            query.setParameter("prenom", prenom);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Utilisateur findByEmail(String email) {
        try {
            System.out.println("GestionUtilisateur.findByEmail called with: '" + email + "'");
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email",
                    Utilisateur.class);
            query.setParameter("email", email);
            Utilisateur result = query.getSingleResult();
            System.out.println(
                    "Query executed successfully, found user: " + (result != null ? result.getEmail() : "null"));
            return result;
        } catch (Exception e) {
            System.out.println("Exception in findByEmail: " + e.getMessage());
            // Return null if no user is found or if there's an error
            return null;
        }
    }

    @Override
    public Utilisateur update(Utilisateur utilisateur) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(utilisateur);
            tr.commit();
            return utilisateur;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(int id) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            if (utilisateur != null) {
                em.remove(utilisateur);
                tr.commit();
                return true;
            }
            tr.rollback();
            return false;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Utilisateur utilisateur) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(utilisateur);
            tr.commit();
            return true;
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long count() {
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM Utilisateur u", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}