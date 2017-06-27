package com.bokine.agendamento.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.model.StatusAgendamento;
import com.bokine.agendamento.repository.Agendamentos;
import com.bokine.agendamento.repository.filter.AgendamentoFilter;

@Named
@ViewScoped
public class PesquisaAgendamentosBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Agendamentos agendamentos;
	
	private AgendamentoFilter filtro;
	private LazyDataModel<Agendamento> model;
	
	public PesquisaAgendamentosBean() {
		filtro = new AgendamentoFilter();
		
		model = new LazyDataModel<Agendamento>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Agendamento> load(int first, int pageSize, String sortField, SortOrder sortOrder, 
					Map<String, Object> filters) {
				
				filtro.setPrimeiroRegistro(first);
				filtro.setQuantidadeRegistros(pageSize);
				filtro.setPropriedadeOrdenacao(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));
				if(filtro.getDataCriacaoAte()!=null){
					filtro.setDataCriacaoAte(limitaAte(filtro.getDataCriacaoAte()));
				}
				if(filtro.getDataMontagemAte()!=null){
					filtro.setDataMontagemAte(limitaAte(filtro.getDataMontagemAte()));
				}
				setRowCount(agendamentos.quantidadeFiltrados(filtro));
				
				return agendamentos.filtrados(filtro);
			}
			
		};
	}
	
	private Date limitaAte(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.set(Calendar.HOUR, 23); 
        date.set(Calendar.MINUTE, 59);
         
        return date.getTime();
    }

	public void posProcessarXls(Object documento) {
		HSSFWorkbook planilha = (HSSFWorkbook) documento;
		HSSFSheet folha = planilha.getSheetAt(0);
		HSSFRow cabecalho = folha.getRow(0);
		HSSFCellStyle estiloCelula = planilha.createCellStyle();
		Font fonteCabecalho = planilha.createFont();
		
		fonteCabecalho.setColor(IndexedColors.WHITE.getIndex());
		fonteCabecalho.setBold(true);
		fonteCabecalho.setFontHeightInPoints((short) 16);
		
		estiloCelula.setFont(fonteCabecalho);
		estiloCelula.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		estiloCelula.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		for (int i = 0; i < cabecalho.getPhysicalNumberOfCells(); i++) {
			cabecalho.getCell(i).setCellStyle(estiloCelula);
		}
	}
	
	public StatusAgendamento[] getStatuses() {
		return StatusAgendamento.values();
	}
	
	public AgendamentoFilter getFiltro() {
		return filtro;
	}

	public LazyDataModel<Agendamento> getModel() {
		return model;
	}
	
}