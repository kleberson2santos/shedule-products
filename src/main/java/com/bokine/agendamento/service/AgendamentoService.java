package com.bokine.agendamento.service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.bokine.agendamento.model.Cliente;
import com.bokine.agendamento.model.NotaFiscal;
import com.bokine.agendamento.util.jpa.Transactional;

public class AgendamentoService implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @return Buscar notaFiscals e do banco Corporatvo
	 */
	@Transactional
	public Set<NotaFiscal> buscarSaidas() {
		return new HashSet<>();
	}
	
	@Transactional
	public Set<Cliente> buscarClientes() {
		return new HashSet<>();
	}
}
