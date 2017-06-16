package com.algaworks.pedidovenda.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.repository.Agendamentos;

@FacesConverter(forClass = Agendamento.class)
public class AgendamentoConverter implements Converter {

	@Inject
	private Agendamentos agendamentos;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Agendamento retorno = null;
		
		if (StringUtils.isNotEmpty(value)) {
			Long id = new Long(value);
			retorno = agendamentos.porId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Agendamento agendamento = (Agendamento) value;
			return agendamento.getId() == null ? null : agendamento.getId().toString();
		}
		
		return "";
	}

}
