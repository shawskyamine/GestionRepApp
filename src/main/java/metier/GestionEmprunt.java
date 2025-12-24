package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import dao.Emprunt;
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
        // Validation des données selon le diagramme
        if (emprunt == null) {
            throw new EmpruntInvalideException("L'emprunt ne peut pas être null");
        }
        
        if (emprunt.getMontant() <= 0) {
            throw new EmpruntInvalideException("Le montant doit être positif");
        }
        
        if (emprunt.getPreteurId() <= 0) {
            throw new EmpruntInvalideException("L'ID du prêteur est obligatoire");
        }
        
        // Initialiser les valeurs par défaut
        if (emprunt.getDateEmprunt() == null) {
            emprunt.setDateEmprunt(new Date());
        }
        
        if (emprunt.getEtat() == null || emprunt.getEtat().trim().isEmpty()) {
            emprunt.setEtat("EN_COURS");
        }
        
        // Date de retour initialisée à null par défaut
        if (emprunt.getDateRetour() == null) {
            emprunt.setDateRetour(null);
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
    public void supprimer(int id) throws EmpruntInvalideException, EmpruntInexistantException, OperationImpossibleException {
        if (id <= 0) {
            throw new EmpruntInvalideException("L'ID de l'emprunt est invalide");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Emprunt emprunt = entityManager.find(Emprunt.class, id);
            
            if (emprunt == null) {
                throw new EmpruntInexistantException(id);
            }
            
            // Vérification que l'emprunt n'est pas déjà terminé
            if ("TERMINE".equals(emprunt.getEtat()) || "REMBOURSE".equals(emprunt.getEtat())) {
                throw new OperationImpossibleException("Impossible de supprimer un emprunt déjà terminé ou remboursé");
            }
            
            entityManager.remove(emprunt);
            transaction.commit();
        } catch (EmpruntInexistantException | OperationImpossibleException e) {  // REMOVED: EmpruntInvalideException
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
        // Validation des données selon le diagramme
        if (emprunt == null) {
            throw new EmpruntInvalideException("L'emprunt ne peut pas être null");
        }
        
        if (emprunt.getId() <= 0) {
            throw new EmpruntInvalideException("L'ID de l'emprunt est invalide");
        }
        
        if (emprunt.getMontant() <= 0) {
            throw new EmpruntInvalideException("Le montant doit être positif");
        }
        
        if (emprunt.getPreteurId() <= 0) {
            throw new EmpruntInvalideException("L'ID du prêteur est obligatoire");
        }
        
        if (emprunt.getEtat() == null || emprunt.getEtat().trim().isEmpty()) {
            throw new EmpruntInvalideException("L'état de l'emprunt est obligatoire");
        }
        
        // Vérification que l'emprunt existe
        Emprunt existant = rechercher(emprunt.getId());
        if (existant == null) {
            throw new EmpruntInexistantException(emprunt.getId());
        }
        
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
    public Emprunt rechercher(int id) {
        if (id <= 0) {
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
    public void rembourser(int id) throws EmpruntInvalideException, EmpruntInexistantException, EmpruntDejaRembourseException, OperationImpossibleException {
        if (id <= 0) {
            throw new EmpruntInvalideException("L'ID de l'emprunt est invalide");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Emprunt emprunt = entityManager.find(Emprunt.class, id);
            
            if (emprunt == null) {
                throw new EmpruntInexistantException(id);
            }
            
            if ("REMBOURSE".equals(emprunt.getEtat())) {
                throw new EmpruntDejaRembourseException(id);
            }
            
            emprunt.setEtat("REMBOURSE");
            emprunt.setDateRetour(new Date()); // Set return date when repaid
            entityManager.merge(emprunt);
            transaction.commit();
        } catch (EmpruntInexistantException | EmpruntDejaRembourseException e) {  // REMOVED: EmpruntInvalideException
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
    public void retourner(int id, Date dateRetour) throws EmpruntInvalideException, EmpruntInexistantException, OperationImpossibleException {
        if (id <= 0) {
            throw new EmpruntInvalideException("L'ID de l'emprunt est invalide");
        }
        
        if (dateRetour == null) {
            throw new EmpruntInvalideException("La date de retour est obligatoire");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Emprunt emprunt = entityManager.find(Emprunt.class, id);
            
            if (emprunt == null) {
                throw new EmpruntInexistantException(id);
            }
            
            emprunt.setDateRetour(dateRetour);
            if ("EN_COURS".equals(emprunt.getEtat())) {
                emprunt.setEtat("RETOURNE");
            }
            
            entityManager.merge(emprunt);
            transaction.commit();
        } catch (EmpruntInexistantException e) {  // REMOVED: EmpruntInvalideException
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors du retour de l'emprunt: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Emprunt> listerParEtat(String etat) throws OperationImpossibleException {
        try {
            TypedQuery<Emprunt> query = entityManager.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.etat = :etat ORDER BY e.dateEmprunt DESC", 
                    Emprunt.class);
            query.setParameter("etat", etat);
            return query.getResultList();
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du listage des emprunts par état: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Emprunt> listerParPreteur(int preteurId) throws OperationImpossibleException {
        try {
            TypedQuery<Emprunt> query = entityManager.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.preteurId = :preteurId ORDER BY e.dateEmprunt DESC", 
                    Emprunt.class);
            query.setParameter("preteurId", preteurId);
            return query.getResultList();
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du listage des emprunts par prêteur: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Emprunt> listerParDateEmprunt(Date dateDebut, Date dateFin) throws OperationImpossibleException {
        try {
            TypedQuery<Emprunt> query = entityManager.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.dateEmprunt BETWEEN :dateDebut AND :dateFin ORDER BY e.dateEmprunt DESC", 
                    Emprunt.class);
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);
            return query.getResultList();
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du listage des emprunts par date: " + e.getMessage(), e);
        }
    }
    
    @Override
    public double getMontantTotalEmprunte() throws OperationImpossibleException {
        try {
            Double total = entityManager
                    .createQuery("SELECT SUM(e.montant) FROM Emprunt e", Double.class)
                    .getSingleResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du calcul du total des emprunts: " + e.getMessage(), e);
        }
    }
    
    @Override
    public double getMontantTotalNonRembourse() throws OperationImpossibleException {
        try {
            Double total = entityManager
                    .createQuery("SELECT SUM(e.montant) FROM Emprunt e WHERE e.etat != 'REMBOURSE'", Double.class)
                    .getSingleResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du calcul du total non remboursé: " + e.getMessage(), e);
        }
    }
    
    /**
     * Méthode utilitaire pour vérifier si un emprunt existe
     */
    private boolean existe(int id) {
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
}