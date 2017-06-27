package com.bokine.agendamento.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "item_montagem")
public class ItemMontagem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Produto produto;
	private Agendamento agendamento;
	private StatusItem statusItem = StatusItem.PENDENTE;
	private String notaFiscal;

	public ItemMontagem(){}
	
	public ItemMontagem(Produto produto) {
		this.produto = produto;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="produto_id",nullable = false)
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
	@NotNull
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "agendamento_id",nullable = false)
	public Agendamento getAgendamento() {
		return agendamento;
	}

	public void setAgendamento(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	public StatusItem getStatusItem() {
		return statusItem;
	}
	public void setStatusItem(StatusItem statusItem) {
		this.statusItem = statusItem;
	}
	
	public String getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(String notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemMontagem other = (ItemMontagem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "[Id:"+
				id+
		" produto:"+
		produto+
		" statusItem:"+
		statusItem
				+ "]";
	}
	

}
