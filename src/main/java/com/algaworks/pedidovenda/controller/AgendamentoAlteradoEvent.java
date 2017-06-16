package com.algaworks.pedidovenda.controller;

import com.algaworks.pedidovenda.model.Agendamento;

public class AgendamentoAlteradoEvent {

	private Agendamento agendamento;
	
	public AgendamentoAlteradoEvent(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

	public Agendamento getAgendamento() {
		return agendamento;
	}
	
}
