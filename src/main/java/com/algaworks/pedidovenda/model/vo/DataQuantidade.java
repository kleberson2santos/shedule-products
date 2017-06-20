package com.algaworks.pedidovenda.model.vo;

import java.io.Serializable;
import java.util.Date;

public class DataQuantidade implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date data;
	private Long valor;

	public DataQuantidade() {
	}
	
	public DataQuantidade(Date data, Long valor) {
		super();
		this.data = data;
		this.valor = valor;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Long getValor() {
		return valor;
	}

	public void setValor(Long valor) {
		this.valor = valor;
	}

}