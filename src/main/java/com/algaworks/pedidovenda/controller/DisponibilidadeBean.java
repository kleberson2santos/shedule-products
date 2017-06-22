package com.algaworks.pedidovenda.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.model.Cliente;
import com.algaworks.pedidovenda.repository.Agendamentos;

@Named
@ViewScoped
public class DisponibilidadeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Agendamentos agendamentos;
	
	private List<Agendamento> agendamentosFiltrados;
	
	private ScheduleModel eventModel;
	
	private ScheduleModel lazyEventModel;
	
	private ScheduleEvent event = new DefaultScheduleEvent();
	
	@PostConstruct
	public void init() {
		lazyEventModel = new LazyScheduleModel(){
			private static final long serialVersionUID = 1L;
			@Override
			public void loadEvents(Date start, Date end) {
				agendamentosFiltrados = agendamentos.filtradosEntre(start, end);
				for (Agendamento agendamento : agendamentosFiltrados) {
					lazyEventModel.addEvent(new DefaultScheduleEvent(agendamento.getCliente().getNome(), agendamento.getDataMontagem(), agendamento.getDataMontagem()));
				}
			}   
		};
	}
	
	public void selecionar(Date dataMontagem) {
		RequestContext.getCurrentInstance().closeDialog(dataMontagem);
	}
	
	public void abrirDialogo() {
		Map<String, Object> opcoes = new HashMap<>();
		opcoes.put("modal", true);
		opcoes.put("resizable", false);
		opcoes.put("height", 550);
		opcoes.put("width",640);
		
		RequestContext.getCurrentInstance().openDialog("/dialogos/Disponibilidade", opcoes, null);
	}

	public List<Agendamento> getAgendamentosFiltrados() {
		return agendamentosFiltrados;
	}

	public void setAgendamentosFiltrados(List<Agendamento> agendamentosFiltrados) {
		this.agendamentosFiltrados = agendamentosFiltrados;
	}
 
     
    public ScheduleModel getEventModel() {
        return eventModel;
    }
     
    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }
     
    public ScheduleEvent getEvent() {
        return event;
    }
 
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
     
    public void addEvent(ActionEvent actionEvent) {
        if(event.getId() == null)
            eventModel.addEvent(event);
        else
            eventModel.updateEvent(event);
         
        event = new DefaultScheduleEvent();
    }
     
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        System.out.println("EVENTO SELECIONADO - ");
        System.out.println(" > "+(ScheduleEvent)selectEvent.getObject());
    }
     
    public void onDateSelect(SelectEvent selectEvent) {
        System.out.println("DATA SELECIONADA - "+ (Date) selectEvent.getObject());
        selecionar((Date) selectEvent.getObject());
    }


}