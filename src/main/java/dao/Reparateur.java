package dao;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Reparateur extends Utilisateur {

    @Column(length = 100)
    private String email;
    
    @Column
    private int pourcentage;
    
    @Column(length = 20)
    private String telephone;
    
    @OneToMany(mappedBy = "reparateur")
    private List<Reparation> reparations;
}