package com.bokine.agendamento.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.model.NotaFiscal;
import com.bokine.agendamento.model.StatusAgendamento;
import com.bokine.agendamento.repository.Agendamentos;
import com.bokine.agendamento.security.UsuarioLogado;
import com.bokine.agendamento.security.UsuarioSistema;

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


