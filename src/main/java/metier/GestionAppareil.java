package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import dao.Appareil;

public class GestionAppareil implements IGestionAppareil {

    private EntityManager entityManager;

    public GestionAppareil(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void ajouter(Appareil appareil) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(appareil);
        transaction.commit();
    }

    @Override
    public void supprimer(int id) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Appareil appareil = entityManager.find(Appareil.class, id);
        if (appareil != null) {
            entityManager.remove(appareil);
        }

        transaction.commit();
    }

    @Override
    public void modifier(Appareil appareil) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(appareil);
        transaction.commit();
    }

    @Override
    public Appareil rechercher(int id) {
        return entityManager.find(Appareil.class, id);
    }

    @Override
    public List<Appareil> lister() {
        return entityManager
                .createQuery("SELECT a FROM Appareil a", Appareil.class)
                .getResultList();
    }
}
