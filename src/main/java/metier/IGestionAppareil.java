package metier;

import java.util.List;
import dao.Appareil;

public interface IGestionAppareil {
    public void ajouter(Appareil appareil);
    public void supprimer(int id);
    public void modifier(Appareil appareil);
    public Appareil rechercher(int id);
    public List<Appareil> lister();
}