package dao;

import javax.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Reparateur extends Utilisateur {

    @Column
    private int pourcentage;

    @Column
    private String telephone;

    // One Reparateur has one Caisse
    @OneToOne(mappedBy = "reparateur", cascade = CascadeType.ALL)
    private Caisse caisse;

    // One Reparateur can have many Reparations
    @OneToMany(mappedBy = "reparateur", cascade = CascadeType.ALL)
    private List<Reparation> reparations;

    @Override
    public String toString() {
        return "Reparateur{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", role='" + getRole() + '\'' + // FIXED: added quotes for String role
                ", pourcentage=" + pourcentage +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}