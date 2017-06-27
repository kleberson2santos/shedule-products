package com.bokine.agendamento.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

import com.bokine.agendamento.model.Usuario;
import com.bokine.agendamento.repository.Agendamentos;
import com.bokine.agendamento.security.UsuarioLogado;
import com.bokine.agendamento.security.UsuarioSistema;

@Named
@RequestScoped
public class GraficoAgendamentosCriadosBean {

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM");
	
	@Inject
	private Agendamentos agendamentos;
	
	@Inject
	@UsuarioLogado
	private UsuarioSistema usuarioLogado;
	
	private LineChartModel model;

	public void preRender() {
		this.model = new LineChartModel();
		this.model.setTitle("Agendamentos criados");
		this.model.setLegendPosition("e");
		this.model.setAnimate(true);
		
		this.model.getAxes().put(AxisType.X, new CategoryAxis());
		
		adicionarSerie("Todos os agendamentos", null);
		adicionarSerie("Meus agendamentos", usuarioLogado.getUsuario());
	}
	
	private void adicionarSerie(String rotulo, Usuario criadoPor) {
		Map<Date, Long> quantidadePorData = this.agendamentos.valoresPorData(15, criadoPor);
		
		ChartSeries series = new ChartSeries(rotulo);
		
		for (Date data : quantidadePorData.keySet()) {
			series.set(DATE_FORMAT.format(data), quantidadePorData.get(data));
		}
		
		this.model.addSeries(series);
	}

	public LineChartModel getModel() {
		return model;
	}
	
}
