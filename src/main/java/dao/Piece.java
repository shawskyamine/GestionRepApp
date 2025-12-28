package dao;

import javax.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String nomPiece;
    
    // CORRECTED: ManyToMany relationship with Appareil (0..* to 0..*)
    // One Piece can belong to many Appareils, and one Appareil can have many Pieces
    @ManyToMany(mappedBy = "pieces")
    private List<Appareil> appareils;
}