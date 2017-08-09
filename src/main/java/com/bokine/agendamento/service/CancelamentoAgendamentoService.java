package com.bokine.agendamento.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.model.ItemMontagem;
import com.bokine.agendamento.model.StatusAgendamento;
import com.bokine.agendamento.model.StatusItem;
import com.bokine.agendamento.repository.Agendamentos;
import com.bokine.agendamento.util.jpa.Transactional;

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
		
		agendamento.getItens().stream()
		.filter((ItemMontagem i) -> i.getStatusItem().equals(StatusItem.PENDENTE))
		.forEach(item->item.setStatusItem(StatusItem.CANCELADO));
		
		agendamento.setStatus(StatusAgendamento.CANCELADO);
		
		agendamento = this.agendamentos.guardar(agendamento);
		
		return agendamento;
	}

}
