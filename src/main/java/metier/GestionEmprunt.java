package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import dao.Emprunt;

public class GestionEmprunt implements IGestionEmprunt {

    private EntityManager entityManager;

    public GestionEmprunt(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void ajouter(Emprunt emprunt) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(emprunt);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Emprunt emprunt = entityManager.find(Emprunt.class, id);
            if (emprunt != null) {
                entityManager.remove(emprunt);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Emprunt emprunt) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(emprunt);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Emprunt rechercher(int id) {
        return entityManager.find(Emprunt.class, id);
    }

    @Override
    public List<Emprunt> lister() {
        return entityManager
                .createQuery("SELECT e FROM Emprunt e", Emprunt.class)
                .getResultList();
    }
}
