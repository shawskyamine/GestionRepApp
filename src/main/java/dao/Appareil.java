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
public class Appareil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String couleur;

    @Column
    private String imei;

    @Column
    private String marque;

    @Column
    private String modele;

    @Column
    private String typeAppareil;

    // CORRECTED: ManyToMany relationship with Piece (0..* to 0..*)
    // One Appareil can contain many Pieces, and one Piece can belong to many
    // Appareils
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "appareil_piece", joinColumns = @JoinColumn(name = "appareil_id"), inverseJoinColumns = @JoinColumn(name = "piece_id"))
    private List<Piece> pieces;

    // Note: No direct relationship to Reparation
    // Reparation owns the relationship with +Repare (1..*)

    @Override
    public String toString() {
        return "Appareil{" +
                "id=" + id +
                ", couleur='" + couleur + '\'' +
                ", imei='" + imei + '\'' +
                ", marque='" + marque + '\'' +
                ", modele='" + modele + '\'' +
                ", typeAppareil='" + typeAppareil + '\'' +
                '}';
    }
}