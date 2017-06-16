package com.algaworks.pedidovenda.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.validator.constraints.NotBlank;
import org.primefaces.event.SelectEvent;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.model.Cliente;
import com.algaworks.pedidovenda.model.ItemMontagem;
import com.algaworks.pedidovenda.model.NotaFiscal;
import com.algaworks.pedidovenda.repository.Clientes;
import com.algaworks.pedidovenda.repository.filter.ClienteFilter;
import com.algaworks.pedidovenda.service.CadastroAgendamentoService;
import com.algaworks.pedidovenda.service.NegocioException;
import com.algaworks.pedidovenda.util.jsf.FacesUtil;

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
	
	private Cliente clienteFilter;
	private List<NotaFiscal> saidasFirebird;
	private ClienteFilter filtro;
	private List<NotaFiscal> notasSelecionadas = new ArrayList<NotaFiscal>();
	private ArrayList<Integer> saidasCodigos;
	
	private ItemMontagem itemSelecionado;

	public CadastroAgendamentoBean() {
		limpar();
	}
	
	public void inicializar() {
		if (this.agendamento == null) {
			limpar();
		}else{
			System.out.println(" >>AGENDAMENTO PARA EDITAR: ");
			System.out.println(" >>: "+agendamento);
			setSaidasSelecionadas(agendamento.getSaidas());	
			System.out.println("Saidas Firebird = Saidas Selecionadas : "+saidasFirebird);			
		}
		
//		this.vendedores = this.usuarios.vendedores();
//		
//		this.pedido.adicionarItemVazio();
//		
//		this.recalcularPedido();
	}

	private void limpar() {
		agendamento = new Agendamento();
		clienteFilter = new Cliente();
		filtro = new ClienteFilter();
		saidasFirebird = new ArrayList<>();
		notasSelecionadas = new ArrayList<NotaFiscal>();
		saidasCodigos = new ArrayList<Integer>();
		agendamento.getItens().clear();
	}

	public void clienteSelecionado(SelectEvent event){
		this.clienteFilter = (Cliente)event.getObject();
		System.out.println("CLIENTEFILTER : "+clienteFilter);
		atualizarSaidas();
		agendamento.setCliente((Cliente)event.getObject());		
	}

	private void atualizarSaidas() {
		System.out.println(" 2 - ATUALIZANDO SAIDAS PELO CLIENTE SELECIONADO");
		saidasFirebird = new ArrayList<NotaFiscal>();
		notasSelecionadas.clear();
		

		List<NotaFiscal> saidasAgendadas = clientes.buscarSaidasSistema(clienteFilter);
		System.out.println("LISTA DE SAIDAS AGENDADAS PARA O CLIENTE");
		System.out.println(saidasAgendadas);
		
		System.out.println("SAIDAS QUERY: " + saidasAgendadas);
		saidasFirebird = clientes.buscarSaidasPorCliente(this.clienteFilter);
		
		//novo
		this.agendamento.setSaidas(saidasFirebird);

	}

	private void limparSaidas() {
		System.out.println("LIMPANDO SAIDAS INTEIROS E ITENS");
		this.agendamento.removerItens();
		this.saidasCodigos.clear();

	}

	public void atualizarProdutos() {
		System.out.println(" 3 - ENVIA SAIDAS SELECIONADAS E BUSCA PRODUTOS: ");
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
		return clienteFilter;
	}

	public void setCliente(Cliente cliente) {
		this.clienteFilter = cliente;
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
		
		this.saidasFirebird = clientes.buscarSaidasPorCliente(agendamento.getCliente());
		
		return nome;
	}
	
	public void setNomeCliente(String nome) {
	}
	
	// UTILS
	
	public boolean isEditando() {
		return this.agendamento.getId() != null;
	}
	
	public void agendamentoAlterado(@Observes AgendamentoAlteradoEvent event) {
		this.agendamento = event.getAgendamento();
	}
	

	public void salvar() {

		agendamento.getItens().forEach(i -> System.out.println(">> Item com Produto Persistido: " + i));
		System.out.println(" >> Agendamento a salvar: " + agendamento);

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
