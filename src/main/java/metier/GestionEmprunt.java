package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import dao.Emprunt;
import dao.Reparateur;
import exception.EmpruntDejaRembourseException;
import exception.EmpruntInvalideException;
import exception.EmpruntInexistantException;
import exception.OperationImpossibleException;

public class GestionEmprunt implements IGestionEmprunt {

    private EntityManager entityManager;

    public GestionEmprunt(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void ajouter(Emprunt emprunt) throws EmpruntInvalideException, OperationImpossibleException {
        // Validation des données
        if (emprunt == null) {
            throw new EmpruntInvalideException("L'emprunt ne peut pas être null");
        }
        
        if (emprunt.getMontant() == null || emprunt.getMontant() <= 0) {
            throw new EmpruntInvalideException("Le montant doit être positif");
        }
        
        if (emprunt.getReparateur() == null) {
            throw new EmpruntInvalideException("Le réparateur est obligatoire");
        }
        
        if (emprunt.getMotif() == null || emprunt.getMotif().trim().isEmpty()) {
            throw new EmpruntInvalideException("Le motif est obligatoire");
        }
        
        // Initialiser les valeurs par défaut
        if (emprunt.getDateEmprunt() == null) {
            emprunt.setDateEmprunt(new Date());
        }
        
        if (emprunt.getStatut() == null) {
            emprunt.setStatut("EN_COURS");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(emprunt);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors de l'ajout de l'emprunt: " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(Long id) throws EmpruntInexistantException, OperationImpossibleException {
        if (id == null || id <= 0) {
            throw new EmpruntInvalideException("L'ID de l'emprunt est invalide");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Emprunt emprunt = entityManager.find(Emprunt.class, id);
            
            if (emprunt == null) {
                throw new EmpruntInexistantException(id);
            }
            
            // Vérification que l'emprunt n'est pas déjà remboursé
            if ("REMBOURSE".equals(emprunt.getStatut())) {
                throw new OperationImpossibleException("Impossible de supprimer un emprunt déjà remboursé");
            }
            
            entityManager.remove(emprunt);
            transaction.commit();
        } catch (EmpruntInexistantException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors de la suppression de l'emprunt: " + e.getMessage(), e);
        }
    }

    @Override
    public void modifier(Emprunt emprunt) throws EmpruntInvalideException, EmpruntInexistantException, OperationImpossibleException {
        // Validation des données
        if (emprunt == null) {
            throw new EmpruntInvalideException("L'emprunt ne peut pas être null");
        }
        
        if (emprunt.getId() == null || emprunt.getId() <= 0) {
            throw new EmpruntInvalideException("L'ID de l'emprunt est invalide");
        }
        
        if (emprunt.getMontant() == null || emprunt.getMontant() <= 0) {
            throw new EmpruntInvalideException("Le montant doit être positif");
        }
        
        if (emprunt.getMotif() == null || emprunt.getMotif().trim().isEmpty()) {
            throw new EmpruntInvalideException("Le motif est obligatoire");
        }
        
        // Vérification que l'emprunt existe
        Emprunt existant = rechercher(emprunt.getId());
        if (existant == null) {
            throw new EmpruntInexistantException(emprunt.getId());
        }
        
        // Ne pas modifier le statut via cette méthode (utiliser rembourser() pour ça)
        emprunt.setStatut(existant.getStatut());
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(emprunt);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors de la modification de l'emprunt: " + e.getMessage(), e);
        }
    }

    @Override
    public Emprunt rechercher(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return entityManager.find(Emprunt.class, id);
    }

    @Override
    public List<Emprunt> lister() throws OperationImpossibleException {
        try {
            return entityManager
                    .createQuery("SELECT e FROM Emprunt e ORDER BY e.dateEmprunt DESC", Emprunt.class)
                    .getResultList();
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du listage des emprunts: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void rembourser(Long id) throws EmpruntInexistantException, EmpruntDejaRembourseException, OperationImpossibleException {
        if (id == null || id <= 0) {
            throw new EmpruntInvalideException("L'ID de l'emprunt est invalide");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Emprunt emprunt = entityManager.find(Emprunt.class, id);
            
            if (emprunt == null) {
                throw new EmpruntInexistantException(id);
            }
            
            if ("REMBOURSE".equals(emprunt.getStatut())) {
                throw new EmpruntDejaRembourseException(id);
            }
            
            emprunt.setStatut("REMBOURSE");
            entityManager.merge(emprunt);
            transaction.commit();
        } catch (EmpruntInexistantException | EmpruntDejaRembourseException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors du remboursement de l'emprunt: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Emprunt> listerParStatut(String statut) throws OperationImpossibleException {
        try {
            TypedQuery<Emprunt> query = entityManager.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.statut = :statut ORDER BY e.dateEmprunt DESC", 
                    Emprunt.class);
            query.setParameter("statut", statut);
            return query.getResultList();
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du listage des emprunts par statut: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Emprunt> listerParReparateur(Long reparateurId) throws OperationImpossibleException {
        try {
            TypedQuery<Emprunt> query = entityManager.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.reparateur.id = :reparateurId ORDER BY e.dateEmprunt DESC", 
                    Emprunt.class);
            query.setParameter("reparateurId", reparateurId);
            return query.getResultList();
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du listage des emprunts par réparateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Méthode utilitaire pour vérifier si un emprunt existe
     */
    private boolean existe(Long id) {
        try {
            Long count = entityManager
                    .createQuery("SELECT COUNT(e) FROM Emprunt e WHERE e.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Méthode pour calculer le total des emprunts en cours
     */
    public Double calculerTotalEmpruntsEnCours() throws OperationImpossibleException {
        try {
            Double total = entityManager
                    .createQuery("SELECT SUM(e.montant) FROM Emprunt e WHERE e.statut = 'EN_COURS'", Double.class)
                    .getSingleResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du calcul du total des emprunts: " + e.getMessage(), e);
        }
    }
}