package com.algaworks.pedidovenda.util.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

@ApplicationScoped
public class EntityManagerProducer {

	private EntityManagerFactory factory;
	private EntityManagerFactory factoryCorporativo;
	
	public EntityManagerProducer() {
		factory = Persistence.createEntityManagerFactory("PedidoPU");
		factoryCorporativo = Persistence.createEntityManagerFactory("Corporativo");
	}
		
	
	@Produces @RequestScoped @Default
	public Session createEntityManager() {
		return (Session) factory.createEntityManager();
	}
	
	@Produces @RequestScoped @Corporativo
	public Session createEntityManagerCorporativo() {
		return (Session) factoryCorporativo.createEntityManager();
	}
	
	public void closeEntityManager(@Disposes Session manager) {
		manager.close();
	}
	
}