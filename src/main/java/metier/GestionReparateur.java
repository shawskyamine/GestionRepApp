package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import dao.Reparateur;
import dao.Reparation;
import dao.Caisse;
import exception.DatabaseException;
import exception.EntityNotFoundException;

public class GestionReparateur implements IGestionReparateur {

    private EntityManager em;

    public GestionReparateur() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ClientUP");
        em = emf.createEntityManager();
    }

    @Override
    public void add(Reparateur reparateur) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();

            // Create a caisse for the reparateur
            Caisse caisse = Caisse.builder()
                    .reparateur(reparateur)
                    .build();
            em.persist(caisse);

            // Set the caisse to the reparateur
            reparateur.setCaisse(caisse);

            em.persist(reparateur);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to add reparateur", e);
        }
    }

    @Override
    public void update(Reparateur reparateur) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(reparateur);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to update reparateur", e);
        }
    }

    @Override
    public void delete(Reparateur reparateur) throws DatabaseException {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.merge(reparateur));
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw new DatabaseException("Failed to delete reparateur", e);
        }
    }

    @Override
    public Reparateur findById(int id) throws DatabaseException, EntityNotFoundException {
        try {
            Reparateur reparateur = em.find(Reparateur.class, id);
            if (reparateur == null) {
                throw new EntityNotFoundException("Reparateur not found with id: " + id);
            }
            return reparateur;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to find reparateur by id", e);
        }
    }

    @Override
    public List<Reparateur> findAll() throws DatabaseException {
        try {
            TypedQuery<Reparateur> query = em.createQuery("SELECT r FROM Reparateur r", Reparateur.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve all reparateurs", e);
        }
    }

    public List<Reparateur> findAllByBoutique(dao.Boutique boutique) throws DatabaseException {
        try {
            TypedQuery<Reparateur> query = em.createQuery("SELECT r FROM Reparateur r WHERE r.boutique = :boutique",
                    Reparateur.class);
            query.setParameter("boutique", boutique);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve reparateurs by boutique", e);
        }
    }

    @Override
    public Reparateur findByEmail(String email) throws DatabaseException, EntityNotFoundException {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                    "SELECT r FROM Reparateur r WHERE r.email = :email",
                    Reparateur.class);
            query.setParameter("email", email);
            List<Reparateur> results = query.getResultList();
            if (results.isEmpty()) {
                throw new EntityNotFoundException("Reparateur not found with email: " + email);
            }
            return results.get(0);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to find reparateur by email", e);
        }
    }

    // Additional useful methods

    public Reparateur findByEmailAndPassword(String email, String motDePasse)
            throws DatabaseException, EntityNotFoundException {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                    "SELECT r FROM Reparateur r WHERE r.email = :email AND r.motDePasse = :motDePasse",
                    Reparateur.class);
            query.setParameter("email", email);
            query.setParameter("motDePasse", motDePasse);
            List<Reparateur> results = query.getResultList();
            if (results.isEmpty()) {
                throw new EntityNotFoundException("Reparateur not found with provided credentials");
            }
            return results.get(0);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Failed to authenticate reparateur", e);
        }
    }

    public List<Reparation> findReparationsByReparateur(int reparateurId) {
        try {
            Reparateur reparateur = em.find(Reparateur.class, reparateurId);
            if (reparateur != null) {
                reparateur.getReparations().size();
                return reparateur.getReparations();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long countReparationsByReparateur(int reparateurId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Reparation r WHERE r.reparateur.id = :reparateurId",
                    Long.class);
            query.setParameter("reparateurId", reparateurId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Reparateur> findByPourcentage(int pourcentage) {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                    "SELECT r FROM Reparateur r WHERE r.pourcentage = :pourcentage",
                    Reparateur.class);
            query.setParameter("pourcentage", pourcentage);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Reparateur> findByTelephone(String telephone) {
        try {
            TypedQuery<Reparateur> query = em.createQuery(
                    "SELECT r FROM Reparateur r WHERE r.telephone = :telephone",
                    Reparateur.class);
            query.setParameter("telephone", telephone);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}