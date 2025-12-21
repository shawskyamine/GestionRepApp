package dao;

import javax.persistence.Column;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public class Reparateur extends Utilisateur  {
	@Column(length = 50)
	private double comission;
	@Column(length = 50)
	private String caisseIndividuelle; // type caisse
	
	
}







