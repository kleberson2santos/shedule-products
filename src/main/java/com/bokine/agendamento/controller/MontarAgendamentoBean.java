package com.bokine.agendamento.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.service.MontarAgendamentoService;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jsf.FacesUtil;

@Named
@RequestScoped
public class MontarAgendamentoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private MontarAgendamentoService montarAgendamentoService;
	
	@Inject
	@AgendamentoEdicao
	private Agendamento agendamento;
	
	@Inject
	private Event<AgendamentoAlteradoEvent> agendamentoAlteradoEvent;
	
	public void montarAgendamento() {
		
		try {
			this.agendamento = this.montarAgendamentoService.montar(this.agendamento);
			this.agendamentoAlteradoEvent.fire(new AgendamentoAlteradoEvent(this.agendamento));
			
			FacesUtil.addInfoMessage("Agendamento montado com sucesso!");
		} catch (NegocioException ne) {
			FacesUtil.addErrorMessage(ne.getMessage());
		} 
	}
	
}
