package com.bokine.agendamento.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
import com.bokine.agendamento.model.StatusItem;
import com.bokine.agendamento.repository.Clientes;
import com.bokine.agendamento.repository.Notas;
import com.bokine.agendamento.repository.filter.ClienteFilter;
import com.bokine.agendamento.service.CadastroAgendamentoService;
import com.bokine.agendamento.service.CadastroNotasService;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroAgendamentoBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;
	
	@Inject
	private Notas notas;

	@Inject
	private CadastroAgendamentoService cadastroAgendamentoService;
	
	@Inject
	private CadastroNotasService cadastroNotasService;


	@Produces
	@AgendamentoEdicao
	private Agendamento agendamento;
	
	private Cliente cliente;
	private List<NotaFiscal> saidasFirebird;
	private ClienteFilter filtro;
	private List<NotaFiscal> notasSelecionadas = new ArrayList<NotaFiscal>();
	private ArrayList<Integer> saidasCodigos;
	
	private ItemMontagem itemSelecionado;
	private List<ItemMontagem> itensCancelados = new ArrayList<ItemMontagem>();
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
		System.out.println("Bean - Atualizar saidas que vao pra tela");
		saidasFirebird = new ArrayList<NotaFiscal>();
		notasSelecionadas.clear();
		saidasFirebird = notas.buscarSaidasPorCliente(this.cliente);
		List<NotaFiscal> notasAgendadasCompleta = new ArrayList<NotaFiscal>();
		
		System.out.println("Se essa saida já foi agendada não tras pro bean");
		
		notasAgendadasCompleta = cadastroNotasService.notasAgendadasECompleta(this.cliente);
		
		System.out.println("notasAgendadasCompletas para esse cliente:"+notasAgendadasCompleta.size());
		
		for (NotaFiscal notaFiscal : notasAgendadasCompleta) {
			saidasFirebird.removeIf(i -> i.getNota().equals(notaFiscal.getNota()));
		}
		this.agendamento.setSaidas(saidasFirebird);
	}

	private void limparSaidas() {
		this.agendamento.removerItens();
		this.saidasCodigos.clear();
	}

	public void atualizarItens() {
		limparSaidas();
		List<ItemMontagem> itens = new ArrayList<ItemMontagem>();
		for (NotaFiscal notaFiscal : notasSelecionadas) {
			this.saidasCodigos.add(notaFiscal.getSaida());
		}
		
		itens = clientes.buscarProdutosPorSaidasSelecionadas(saidasCodigos);
		
		
		if(!itens.isEmpty()) {
			for (ItemMontagem itemMontagem : itens) {
				//PRECISO REMOVER OS ITENS QUE NAO ESTAO COM STATUS DE CANCELADO
				System.out.println("Bean - Buscar Itens Cancelados...");
				itensCancelados=cadastroAgendamentoService.buscaItensCancelados(itemMontagem);
			}
		}else {
			FacesUtil.addWarningMessage("A busca não retornou nenhum móvel!");
		}
		System.out.println("Bean - Itens cancelados "+itensCancelados.size());
		if(itensCancelados.isEmpty()) {
			for (ItemMontagem itemMontagem : itens) {
				agendamento.adiciona(itemMontagem);	
			}
		}else{
			for (ItemMontagem item : itens) {
				for(ItemMontagem itemCancelado : itensCancelados) {
					if(item.getNotaFiscal().equals(item.getNotaFiscal())&&item.getProduto().equals(itemCancelado.getProduto())) {
						System.out.println("Bean -Produto é igual:"+item.getProduto().getRotulo()+" Nota:"+item.getNotaFiscal());
						agendamento.adiciona(item);
					}else 
						if(item.getNotaFiscal().equals(item.getNotaFiscal())&&!item.getProduto().equals(itemCancelado.getProduto())){
						System.out.println("Bean - Mesma nota mas nao e o mesmo produto nao adicionar:"+item.getProduto().getRotulo()
								+" Status:"+item.getStatusItem());
						System.out.println("pois o produto cancelado é:"+itemCancelado.getProduto().getRotulo()
								+" Status:"+itemCancelado.getStatusItem());
						}
						else {
							System.out.println("Bean - ADICIONA DE QUALQUER JEITO:"+item.getProduto().getRotulo());
							agendamento.adiciona(item);
						}
				}
			}
		}

	}

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
	
	public boolean isEditando() {
		return this.agendamento.getId() != null;
	}
	
	public void agendamentoAlterado(@Observes AgendamentoAlteradoEvent event) {
		this.agendamento = event.getAgendamento();
	}
	

	public void salvar() throws NegocioException {
		if(!itensCancelados.isEmpty()) {
			ItemMontagem item = new ItemMontagem();
			System.out.println("Atualizar notas canceladas...");
			for (ItemMontagem itemMontagem : itensCancelados) {
				System.out.println("Item cancelado:"+itemMontagem.getId()+" nota:"+itemMontagem.getNotaFiscal()+" - "+itemMontagem.getStatusItem());
				item = cadastroAgendamentoService.atualizarStatusItem(itemMontagem);
				System.out.println("Item atualizado xom sucesso! "+item.getStatusItem());
			}
		}
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
	
	public void alteraStatusAssistencia(ItemMontagem item) {
		item.setStatusItem(StatusItem.ASSISTENCIA);
	}
	public void alteraStatusCancelado(ItemMontagem item) {
		item.setStatusItem(StatusItem.CANCELADO);
	}
	public void alteraStatusMontado(ItemMontagem item) {
		item.setStatusItem(StatusItem.MONTADO);
	}
	public void alteraStatusRemontado(ItemMontagem item) {
		item.setStatusItem(StatusItem.REMONTADO);
	}
	
	public boolean verificaStatusRemontavel(ItemMontagem item) {
		return item.getStatusItem().equals(StatusItem.ASSISTENCIA);
	}
	
	public boolean verificaStatusAssistenciavel(ItemMontagem item) {
		return item.getStatusItem().equals(StatusItem.MONTADO);
	}
	
	public boolean verificaStatusMontavel(ItemMontagem item) {
		return item.getStatusItem().equals(StatusItem.PENDENTE)||item.getStatusItem().equals(StatusItem.CANCELADO);
	}

}
