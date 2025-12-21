package dao;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reparation {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 100, nullable = false)
	private String appareil; //type appareil
	@Column(nullable = false)
	private double prix ;
	@Column(length = 30, nullable = false)
	private String etat;
	private Date dateDebut ;
	private Date dateFinEstimee;
	private Date dateLivraison ;
	@Column(length = 50, unique = true)
	private String codeSuivi ;
	@ManyToOne
	private String client ; //type client 
	
}
