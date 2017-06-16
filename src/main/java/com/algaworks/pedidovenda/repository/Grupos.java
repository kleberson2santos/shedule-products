package com.algaworks.pedidovenda.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.algaworks.pedidovenda.model.Grupo;
import com.algaworks.pedidovenda.service.NegocioException;
import com.algaworks.pedidovenda.util.jpa.Transactional;

public class Grupos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Grupo guardar(Grupo grupo) {		
		return manager.merge(grupo);		
	}
	
	@Transactional
	public void remover(Grupo grupo) throws NegocioException {
		try {
			grupo = porId(grupo.getId());
			manager.remove(grupo);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("Grupo não pode ser excluído.");
		}
	}
	
	public List<Grupo> todos() {
		return manager.createQuery("from Grupo", Grupo.class).getResultList();
	}
	
	
	public Grupo porId(Long id) {
		return manager.find(Grupo.class, id);
	}
}
