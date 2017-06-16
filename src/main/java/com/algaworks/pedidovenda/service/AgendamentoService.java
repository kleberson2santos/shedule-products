package com.algaworks.pedidovenda.service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.algaworks.pedidovenda.model.Cliente;
import com.algaworks.pedidovenda.model.NotaFiscal;
import com.algaworks.pedidovenda.util.jpa.Transactional;

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
