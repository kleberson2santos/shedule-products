package com.bokine.agendamento.model;
public enum StatusItem {

	PENDENTE("Pendente"),
	MONTADO("Montado"),
	CANCELADO("Cancelado"),
	ASSISTENCIA("Assistência"),
	REMONTADO("Remontado"),
	REAGENDADO("Regendado");
	
	private final String descricao;
	
	private StatusItem(String descricao){
		this.descricao = descricao;
	}
 
	public String getDescricao(){
		return descricao;
	}


}
