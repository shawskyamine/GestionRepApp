package metier;

import dao.Piece;
import java.util.List;

public interface IGestionPiece {
    void add(Piece piece);
    void update(Piece piece);
    void delete(Piece piece);
    Piece findById(int id);
    List<Piece> findAll();
    List<Piece> findByNom(String nom);
}