package com.bokine.agendamento.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.bokine.agendamento.service.NegocioException;

@Entity
@Table(name = "agendamento")
public class Agendamento implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Date dataCriacao;
	private Cliente cliente;
	private Date dataMontagem;
	private String observacao;
	private Usuario usuario;
	private StatusAgendamento status = StatusAgendamento.AGENDADO;
	private List<ItemMontagem> itens = new ArrayList<>();
	private List<NotaFiscal> notasFiscais = new ArrayList<NotaFiscal>();
	
	public Agendamento() {
		System.out.println("CONSTRUINDO AGENDAMENTO");
		this.cliente = new Cliente();
		this.cliente.setEndereco(new Endereco());
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
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_criacao" , nullable = false)
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Date string) {
		this.dataCriacao = string;
	}
	
	@NotNull
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "cliente_id", nullable = false)
	public Cliente getCliente() {
		return cliente;
	}
	
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable=false , length=10)
	public StatusAgendamento getStatus() {
		return status;
	}
	public void setStatus(StatusAgendamento status) {
		this.status = status;
	}
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_montagem" , nullable = false)
	public Date getDataMontagem() {
		return dataMontagem;
	}
	public void setDataMontagem(Date dataMontagem) {
		if(isNovo()){
			this.dataMontagem = horaDefault(dataMontagem);
		}else{
			this.dataMontagem = dataMontagem;
		}
		
	}
	
	@NotNull
	@OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ItemMontagem> getItens() {
		return itens;
	}
	public void setItens(List<ItemMontagem> itens) {
		this.itens = itens;
	}
	
	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = true)
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<NotaFiscal> getSaidas() {
		return notasFiscais;
	}
	public void setSaidas(List<NotaFiscal> notasFiscais) {
		this.notasFiscais = notasFiscais;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agendamento other = (Agendamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return "Agendamento [id=" + id + ", dataCriacao=" + dataCriacao + ", cliente=" + cliente + ", dataMontagem="
				+ dataMontagem + ", observacao=" + observacao + ", usuario=" + usuario + ", status=" + status
				+ ", itens=" + itens + ", notasFiscais=" + notasFiscais + "]";
	}
	
	//UTILS
	
	@Transient
	public void removerItens() {
		this.itens.clear();		
	}
	
	public void adicionarSaidas(List<NotaFiscal> notasSelecionadas) throws NegocioException {
		if(notasSelecionadas!=null){
			for (NotaFiscal notaFiscal : notasSelecionadas) {
				notaFiscal.setAgendamento(this);
			}
		}else{
			throw new NegocioException("A Lista de Saidas adicionadas não pode ser null ");
		}
		this.notasFiscais = new ArrayList<NotaFiscal>();
		this.notasFiscais = notasSelecionadas;
	}
	
	@Transient
	public void adiciona(ItemMontagem item) {
		this.itens.add(item);
		item.setAgendamento(this);
	}
	
	@Transient
	public boolean isNovo(){
		return getId()==null;
	}
	
	
	@Transient
	public boolean isExistente(){
		return !isNovo();
	}
	
	@Transient
	public boolean isAgendado() {
		return StatusAgendamento.AGENDADO.equals(this.getStatus());
	}
	
	@Transient
	public boolean isAlteravel() {
		return this.isAgendado();
	}
	
	@Transient
	public boolean isNaoAlteravel() {
		return !this.isAlteravel();
	}
	
	@Transient
	public boolean isNaoMontavel() {
		return !this.isMontavel();
	}

	@Transient
	public boolean isMontavel() {
		return this.isExistente() && this.isAgendado();
	}
	
	@Transient
	public boolean isNaoCancelavel() {
		return !this.isCancelavel();
	}
	
	@Transient
	public boolean isCancelavel() {
		return this.isExistente() && !this.isCancelado();
	}
	
	@Transient
	private boolean isCancelado() {
		return StatusAgendamento.CANCELADO.equals(this.getStatus());
	}
	
	@Transient
	private Date horaDefault(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.set(Calendar.HOUR_OF_DAY, 9);
         
        return date.getTime();
    }

}
