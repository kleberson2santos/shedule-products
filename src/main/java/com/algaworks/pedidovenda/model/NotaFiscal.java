package com.algaworks.pedidovenda.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "nota_fiscal")
public class NotaFiscal implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	private Integer saida;
	private Integer nota;
	private Agendamento agendamento;
	private List<NotaFiscal> notas;
	
	public NotaFiscal(){}
	
	public NotaFiscal(Integer codSaida, Integer nota) {
		this.saida = codSaida;
		this.nota = nota;
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
	@Column(name="saida",unique=true, nullable = false, length = 20)
	public Integer getSaida() {
		return saida;
	}
	
	public void setSaida(Integer saida) {
		this.saida = saida;
	}

	@NotNull
	@Column(name="nota", nullable = false, length = 20)
	public Integer getNota() {
		return nota;
	}

	public void setNota(Integer nota) {
		this.nota = nota;
	}
	
	@ManyToOne
	@JoinColumn(name = "agendamento_id", nullable = false)
	public Agendamento getAgendamento() {
		return agendamento;
	}
	public void setAgendamento(Agendamento agendamento) {
		this.agendamento = agendamento;
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
		NotaFiscal other = (NotaFiscal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NotaFiscal [id=" + id + ", saida=" + saida + ", nota=" + nota + "]";
	}

	

}
