package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import dao.Appareil;
import exception.AppareilDejaExistantException;
import exception.AppareilInvalideException;
import exception.OperationImpossibleException;

public class GestionAppareil implements IGestionAppareil {

    private EntityManager entityManager;

    public GestionAppareil(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void ajouter(Appareil appareil) throws AppareilInvalideException, AppareilDejaExistantException, OperationImpossibleException {
        // Validation des données
        if (appareil == null) {
            throw new AppareilInvalideException("L'appareil ne peut pas être null");
        }
        
        if (appareil.getImei() == null || appareil.getImei().trim().isEmpty()) {
            throw new AppareilInvalideException("L'IMEI est obligatoire");
        }
        
        if (appareil.getType() == null || appareil.getType().trim().isEmpty()) {
            throw new AppareilInvalideException("Le type d'appareil est obligatoire");
        }
        
        // Vérification de l'unicité de l'IMEI
        if (existe(appareil.getImei())) {
            throw new AppareilDejaExistantException(appareil.getImei());
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(appareil);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors de l'ajout de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(String imei) throws AppareilInvalideException, OperationImpossibleException {
        if (imei == null || imei.trim().isEmpty()) {
            throw new AppareilInvalideException("L'IMEI est obligatoire");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Appareil appareil = entityManager.find(Appareil.class, imei);
            
            if (appareil == null) {
                throw new OperationImpossibleException("L'appareil avec l'IMEI " + imei + " n'existe pas");
            }
            
            entityManager.remove(appareil);
            transaction.commit();
        } catch (OperationImpossibleException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Relancer l'exception telle quelle
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors de la suppression de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public void modifier(Appareil appareil) throws AppareilInvalideException, OperationImpossibleException {
        // Validation des données
        if (appareil == null) {
            throw new AppareilInvalideException("L'appareil ne peut pas être null");
        }
        
        if (appareil.getImei() == null || appareil.getImei().trim().isEmpty()) {
            throw new AppareilInvalideException("L'IMEI est obligatoire");
        }
        
        if (appareil.getType() == null || appareil.getType().trim().isEmpty()) {
            throw new AppareilInvalideException("Le type d'appareil est obligatoire");
        }
        
        // Vérification que l'appareil existe
        if (!existe(appareil.getImei())) {
            throw new OperationImpossibleException("L'appareil avec l'IMEI " + appareil.getImei() + " n'existe pas");
        }
        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(appareil);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new OperationImpossibleException("Erreur lors de la modification de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public Appareil rechercher(String imei) {
        if (imei == null || imei.trim().isEmpty()) {
            return null;
        }
        return entityManager.find(Appareil.class, imei);
    }

    @Override
    public List<Appareil> lister() throws OperationImpossibleException {
        try {
            return entityManager
                    .createQuery("SELECT a FROM Appareil a ORDER BY a.type", Appareil.class)
                    .getResultList();
        } catch (Exception e) {
            throw new OperationImpossibleException("Erreur lors du listage des appareils: " + e.getMessage(), e);
        }
    }
    
    /**
     * Méthode utilitaire pour vérifier si un appareil existe
     */
    private boolean existe(String imei) {
        try {
            Long count = entityManager
                    .createQuery("SELECT COUNT(a) FROM Appareil a WHERE a.imei = :imei", Long.class)
                    .setParameter("imei", imei)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}