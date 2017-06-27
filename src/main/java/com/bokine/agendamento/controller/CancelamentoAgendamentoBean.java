package com.bokine.agendamento.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.service.CancelamentoAgendamentoService;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jsf.FacesUtil;

@Named
@RequestScoped
public class CancelamentoAgendamentoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CancelamentoAgendamentoService cancelamentoAgendamentoService;
	
	@Inject
	private Event<AgendamentoAlteradoEvent> agendamentoAlteradoEvent;
	
	@Inject
	@AgendamentoEdicao
	private Agendamento agendamento;
	
	public void cancelarAgendamento() {
		try {
			this.agendamento = this.cancelamentoAgendamentoService.cancelar(this.agendamento);
			this.agendamentoAlteradoEvent.fire(new AgendamentoAlteradoEvent(this.agendamento));
			
			FacesUtil.addInfoMessage("agendamento cancelado com sucesso!");
		} catch (NegocioException ne) {
			FacesUtil.addErrorMessage(ne.getMessage());
		}
	}
	
}
