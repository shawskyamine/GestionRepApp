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
    private String email;
    
    @Column
    private String motDePasse;
    
    @Column
    private int pourcentage;
    
    @Column
    private String telephone;
    
    // One Reparateur can have many Reparations
    @OneToMany(mappedBy = "reparateur", cascade = CascadeType.ALL)
    private List<Reparation> reparations;
}