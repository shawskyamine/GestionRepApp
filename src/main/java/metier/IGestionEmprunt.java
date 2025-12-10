package metier;
import java.util.List;
import dao.Emprunt;

public interface IGestionEmprunt {
	
	    public void ajouter(Emprunt emprunt);
	    public void supprimer(int id);
	    public void modifier(Emprunt emprunt);
	    public Emprunt rechercher(int id);
	    public List<Emprunt> lister();
	

}
