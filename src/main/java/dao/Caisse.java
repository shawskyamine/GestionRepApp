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
public class Caisse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // One Caisse belongs to one Reparateur
    @OneToOne
    @JoinColumn(name = "reparateur_id")
    private Reparateur reparateur;

    // One Caisse is alimented by many Reparations (1..*)
    @OneToMany(mappedBy = "caisse", cascade = CascadeType.ALL)
    private List<Reparation> reparations;

    // One Caisse provides many Emprunts (+Fournit relationship, 1..*)
    @OneToMany(mappedBy = "caisse", cascade = CascadeType.ALL)
    private List<Emprunt> emprunts;
}