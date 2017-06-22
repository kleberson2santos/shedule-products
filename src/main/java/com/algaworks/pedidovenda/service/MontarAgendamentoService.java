package com.algaworks.pedidovenda.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.model.StatusAgendamento;
import com.algaworks.pedidovenda.model.StatusItem;
import com.algaworks.pedidovenda.repository.Agendamentos;
import com.algaworks.pedidovenda.util.jpa.Transactional;

public class MontarAgendamentoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CadastroAgendamentoService cadastroAgendamentoService;
	
	
	
	@Inject
	private Agendamentos agendamentos;
	
	@Transactional
	public Agendamento montar(Agendamento agendamento) throws NegocioException {
		agendamento = this.cadastroAgendamentoService.salvar(agendamento);
		
		if (agendamento.isNaoMontavel()) {
			throw new NegocioException("Agendamento não pode ser montado com status "
					+ agendamento.getStatus().getDescricao() + ".");
		}
		
		agendamento.getItens().forEach(item->item.setStatusItem(StatusItem.MONTADO));		
		agendamento.setStatus(StatusAgendamento.MONTADO);
		
		agendamento = this.agendamentos.guardar(agendamento);
		
		return agendamento;
	}
	
}
