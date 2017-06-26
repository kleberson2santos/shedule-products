package com.algaworks.pedidovenda.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.model.NotaFiscal;
import com.algaworks.pedidovenda.model.StatusAgendamento;
import com.algaworks.pedidovenda.repository.Agendamentos;
import com.algaworks.pedidovenda.security.UsuarioLogado;
import com.algaworks.pedidovenda.security.UsuarioSistema;

public class CadastroAgendamentoService implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	@UsuarioLogado
	private UsuarioSistema usuarioLogado;
	
	@Inject
	private Agendamentos agendamentos;
	
	public Agendamento salvar(Agendamento agendamento){
		if (agendamento.isNovo()) {
			agendamento.setDataCriacao(new Date());
			agendamento.setStatus(StatusAgendamento.AGENDADO);
			agendamento.setUsuario(this.usuarioLogado.getUsuario());
		}
		
		agendamento =  agendamentos.guardar(agendamento);
		return agendamento;
	}


	public List<NotaFiscal> buscarSaidas(Agendamento agendamento) {
		
		return agendamentos.buscarNotas(agendamento);
	}
}	


