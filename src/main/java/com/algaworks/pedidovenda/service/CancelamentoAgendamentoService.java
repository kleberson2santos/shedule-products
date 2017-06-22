package com.algaworks.pedidovenda.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.model.StatusAgendamento;
import com.algaworks.pedidovenda.model.StatusItem;
import com.algaworks.pedidovenda.repository.Agendamentos;
import com.algaworks.pedidovenda.util.jpa.Transactional;

public class CancelamentoAgendamentoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Agendamentos agendamentos;
	
	
	@Transactional
	public Agendamento cancelar(Agendamento agendamento) throws NegocioException {
		agendamento = this.agendamentos.porId(agendamento.getId());
		
		if (agendamento.isNaoCancelavel()) {
			throw new NegocioException("Agendamento não pode ser cancelado no status "
					+ agendamento.getStatus().getDescricao() + ".");
		}
		
		if (agendamento.isAgendado()) {
//			this.estoqueService.retornarItensEstoque(agendamento);
		}
		agendamento.getItens().forEach(item->item.setStatusItem(StatusItem.CANCELADO));
		agendamento.setStatus(StatusAgendamento.CANCELADO);
		
		agendamento = this.agendamentos.guardar(agendamento);
		
		return agendamento;
	}

}
