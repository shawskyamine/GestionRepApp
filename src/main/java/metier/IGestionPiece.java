package metier;

import dao.Piece;
import exception.DatabaseException;
import java.util.List;

public interface IGestionPiece {
    void add(Piece piece) throws DatabaseException;
    void update(Piece piece) throws DatabaseException;
    void delete(Piece piece) throws DatabaseException;
    Piece findById(int id) throws DatabaseException;
    List<Piece> findAll() throws DatabaseException;
    List<Piece> findByNom(String nom) throws DatabaseException;
}