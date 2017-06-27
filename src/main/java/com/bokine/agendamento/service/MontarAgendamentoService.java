package com.bokine.agendamento.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.model.StatusAgendamento;
import com.bokine.agendamento.model.StatusItem;
import com.bokine.agendamento.repository.Agendamentos;
import com.bokine.agendamento.util.jpa.Transactional;

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
