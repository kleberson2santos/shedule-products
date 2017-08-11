package com.bokine.agendamento.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.bokine.agendamento.model.Cliente;
import com.bokine.agendamento.model.NotaFiscal;
import com.bokine.agendamento.repository.Notas;

public class CadastroNotasService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Notas notas;
	
	public List<NotaFiscal> notasAgendadasECompleta(Cliente cliente) {
		List<NotaFiscal> notasRetornados = new ArrayList<NotaFiscal>();
		notasRetornados = notas.buscarNotasAgendadasECompletas(cliente);
		
		return notasRetornados;
	}
	

}
