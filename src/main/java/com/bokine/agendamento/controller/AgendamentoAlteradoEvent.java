package com.bokine.agendamento.controller;

import com.bokine.agendamento.model.Agendamento;

public class AgendamentoAlteradoEvent {

	private Agendamento agendamento;
	
	public AgendamentoAlteradoEvent(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

	public Agendamento getAgendamento() {
		return agendamento;
	}
	
}
