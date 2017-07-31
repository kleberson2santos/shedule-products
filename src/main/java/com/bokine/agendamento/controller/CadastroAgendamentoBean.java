package com.bokine.agendamento.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.validator.constraints.NotBlank;
import org.primefaces.event.SelectEvent;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.model.Cliente;
import com.bokine.agendamento.model.Endereco;
import com.bokine.agendamento.model.ItemMontagem;
import com.bokine.agendamento.model.NotaFiscal;
import com.bokine.agendamento.repository.Clientes;
import com.bokine.agendamento.repository.filter.ClienteFilter;
import com.bokine.agendamento.service.CadastroAgendamentoService;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroAgendamentoBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;

	@Inject
	private CadastroAgendamentoService cadastroAgendamentoService;

	@Produces
	@AgendamentoEdicao
	private Agendamento agendamento;
	
	private Cliente cliente;
	private List<NotaFiscal> saidasFirebird;
	private ClienteFilter filtro;
	private List<NotaFiscal> notasSelecionadas = new ArrayList<NotaFiscal>();
	private ArrayList<Integer> saidasCodigos;
	
	private ItemMontagem itemSelecionado;
	private Date dataAgendamentoSelecionada;

	public CadastroAgendamentoBean() {
		limpar();
	}
	
	public void inicializar() {
		if (this.agendamento == null) {
			limpar();
		}else{
			setSaidasSelecionadas(agendamento.getSaidas());	
			this.cliente = agendamento.getCliente();
		}
	}

	private void limpar() {
		agendamento = new Agendamento();
		cliente = new Cliente();
		cliente.setEndereco(new Endereco());
		filtro = new ClienteFilter();
		saidasFirebird = new ArrayList<>();
		notasSelecionadas = new ArrayList<NotaFiscal>();
		saidasCodigos = new ArrayList<Integer>();
		agendamento.getItens().clear();
		agendamento.setCliente(cliente);
	}

	public void clienteSelecionado(SelectEvent event){
		this.cliente = (Cliente)event.getObject();
		atualizarSaidas();
		agendamento.setCliente((Cliente)event.getObject());		
	}
	
	public void dataAgendamentoSelecionada(SelectEvent event){
		this.agendamento.setDataMontagem((Date)event.getObject());
	}

	private void atualizarSaidas() {
		saidasFirebird = new ArrayList<NotaFiscal>();
		notasSelecionadas.clear();
		saidasFirebird = clientes.buscarSaidasPorCliente(this.cliente);
		this.agendamento.setSaidas(saidasFirebird);
	}

	private void limparSaidas() {
		this.agendamento.removerItens();
		this.saidasCodigos.clear();
	}

	public void atualizarProdutos() {
		limparSaidas();
		notasSelecionadas.forEach(i -> System.out.println("[]-" + i));
		List<ItemMontagem> itens = new ArrayList<ItemMontagem>();
		for (NotaFiscal notaFiscal : notasSelecionadas) {
			this.saidasCodigos.add(notaFiscal.getSaida());
		}
		itens = clientes.buscarProdutosPorSaidasSelecionadas(saidasCodigos);
		if (itens.isEmpty()) {
			FacesUtil.addWarningMessage("A busca não retornou nenhum móvel!");
		}
		for (ItemMontagem itemMontagem : itens) {
			agendamento.adiciona(itemMontagem);
		}
	}

	
	// GETTERS AND SETTERS

	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Agendamento getAgendamento() {
		return agendamento;
	}
	public void setAgendamento(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

	public List<NotaFiscal> getSaidasSelecionadas() {
		return notasSelecionadas;
	}

	public void setSaidasSelecionadas(List<NotaFiscal> saidasSelecionadas) {
		this.notasSelecionadas = saidasSelecionadas;
	}

	public List<NotaFiscal> getSaidasFirebird() {
		return saidasFirebird;
	}

	public ClienteFilter getFiltro() {
		return filtro;
	}

	public List<Integer> getSaidas() {
		return saidasCodigos;
	}
	
	public ItemMontagem getItemSelecionado() {
		return itemSelecionado;
	}
	public void setItemSelecionado(ItemMontagem itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	@NotBlank
	public String getNomeCliente() {
		String nome = agendamento.getCliente() == null ? null : agendamento.getCliente().getNome();
		return nome;
	}
	public void setNomeCliente(String nome) {
	}
	
	public Date getDataAgendamentoSelecionada() {
		return dataAgendamentoSelecionada;
	}
	public void setDataAgendamentoSelecionada(Date dataAgendamentoSelecionada) {
		this.dataAgendamentoSelecionada = dataAgendamentoSelecionada;
	}
	
	
	// UTILS

	public boolean isEditando() {
		return this.agendamento.getId() != null;
	}
	
	public void agendamentoAlterado(@Observes AgendamentoAlteradoEvent event) {
		this.agendamento = event.getAgendamento();
	}
	

	public void salvar() throws NegocioException {
		System.out.println(" Salvar agendamento");
		
		if(!agendamento.isNovo()){
			notasSelecionadas = cadastroAgendamentoService.buscarSaidas(agendamento);
		}
		if (agendamento.getItens().isEmpty()) {
			FacesUtil.addWarningMessage("Informe pelo menos um item no agendamento!");
		} else {
			agendamento.adicionarSaidas(notasSelecionadas);
			agendamento = cadastroAgendamentoService.salvar(this.agendamento);
			FacesUtil.addInfoMessage("Agendamento salvo com sucesso!");
			limpar();
		}

	}
	
	public void excluirItem(){
		try {
			clientes.remover(itemSelecionado);
			agendamento.getItens().remove(itemSelecionado);
			
			FacesUtil.addInfoMessage("Item " + itemSelecionado.getProduto().getRotulo() 
					+ " excluído com sucesso.");
		} catch (NegocioException ne) {
			FacesUtil.addErrorMessage(ne.getMessage());
		}
	}

}
