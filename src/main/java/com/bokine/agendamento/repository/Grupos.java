package com.bokine.agendamento.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.bokine.agendamento.model.Grupo;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jpa.Transactional;

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
