package com.bokine.agendamento.controller;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.util.jsf.FacesUtil;
import com.bokine.agendamento.util.mail.Mailer;
import com.outjected.email.api.MailMessage;
import com.outjected.email.impl.templating.velocity.VelocityTemplate;

@Named
@RequestScoped
public class EnvioAgendamentoEmailBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Mailer mailer;
	
	@Inject
	@AgendamentoEdicao
	private Agendamento agendamento;
	
	public void enviarAgendamento() {
		MailMessage message = mailer.novaMensagem();
		
		message.to(this.agendamento.getCliente().getEmail())
			.subject("Agendamento de Montagem N°" + this.agendamento.getId())
			.bodyHtml(new VelocityTemplate(getClass().getResourceAsStream("/emails/agendamento.template")))
			.put("agendamento", this.agendamento)
			.put("numberTool", new NumberTool())
			.put("dateToll", new DateTool())
			.put("locale", new Locale("pt", "BR"))
			.send();
		
		FacesUtil.addInfoMessage("Agendamento enviado por e-mail com sucesso!");
	}
	
	
}
