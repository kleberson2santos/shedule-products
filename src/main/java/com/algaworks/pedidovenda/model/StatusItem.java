package com.algaworks.pedidovenda.model;
public enum StatusItem {

	PENDENTE("Pendente"),
	MONTADO("Montado"),
	CANCELADO("Cancelado");
	
	private final String descricao;
	
	private StatusItem(String descricao){
		this.descricao = descricao;
	}
 
	public String getDescricao(){
		return descricao;
	}


}
