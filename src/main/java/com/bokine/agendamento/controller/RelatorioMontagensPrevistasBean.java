package com.bokine.agendamento.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.hibernate.Session;

import com.bokine.agendamento.util.jsf.FacesUtil;
import com.bokine.agendamento.util.report.ExecutorRelatorio;

@Named
@RequestScoped
public class RelatorioMontagensPrevistasBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dataInicio;
	private Date dataFim;

	@Inject
	private FacesContext facesContext;

	@Inject
	private HttpServletResponse response;

	@Inject
	private EntityManager manager;

	public void emitir() {
		Map<String, Object> parametros = new HashMap<>();
		
		parametros.put("data_inicio",geraDataInicial(this.dataInicio));
		parametros.put("data_fim",geraDataFinal(this.dataFim));
		ExecutorRelatorio executor = new ExecutorRelatorio("/relatorios/relatorio_previsao_montagens.jasper",
				this.response, parametros, "Previsão de Montagens.pdf");
		
		Session session = manager.unwrap(Session.class);
		session.doWork(executor);
		
		if (executor.isRelatorioGerado()) {
			facesContext.responseComplete();
		} else {
			FacesUtil.addErrorMessage("A execução do relatório não retornou dados.");
		}
	}

	@NotNull
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@NotNull
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	private Date geraDataInicial(Date dataInicio) {
		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(dataInicio);
		dataInicial.set(Calendar.HOUR_OF_DAY, 0);
		dataInicial.set(Calendar.MINUTE, 0);
		dataInicial.set(Calendar.SECOND, 0);
		
		return dataInicial.getTime();
	}
	
	private Date geraDataFinal(Date dataFim) {
		Calendar dataFinal = Calendar.getInstance();
		dataFinal.setTime(dataFim);
		dataFinal.add(Calendar.DAY_OF_MONTH, 1);
		dataFinal.set(Calendar.HOUR_OF_DAY, 23);
		dataFinal.set(Calendar.MINUTE, 59);
		dataFinal.set(Calendar.SECOND, 59);
		
		return dataFinal.getTime();
}

}