package com.bokine.agendamento.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
		saidasFirebird = new ArrayList<NotaFiscal>();
		notasSelecionadas.clear();
		saidasFirebird = notas.buscarSaidasPorCliente(this.cliente);
		List<NotaFiscal> notasAgendadasCompleta = new ArrayList<NotaFiscal>();
		notasAgendadasCompleta = cadastroNotasService.notasAgendadasECompleta(this.cliente);
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
				itensCancelados=cadastroAgendamentoService.buscaItensCancelados(itemMontagem);
			}
		}else {
			FacesUtil.addWarningMessage("A busca não retornou nenhum móvel!");
		}
		if(itensCancelados.isEmpty()) {
			for (ItemMontagem itemMontagem : itens) {
				agendamento.adiciona(itemMontagem);	
			}
		}else{
			for (ItemMontagem item : itens) {
				for(ItemMontagem itemCancelado : itensCancelados) {
					if(item.getNotaFiscal().equals(item.getNotaFiscal())&&item.getProduto().equals(itemCancelado.getProduto())) {
						agendamento.adiciona(item);
					}else 
						if(item.getNotaFiscal().equals(item.getNotaFiscal())&&!item.getProduto().equals(itemCancelado.getProduto())){
						}
						else {
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
		List<NotaFiscal>notasSelecionadasTemp = notasSelecionadas;
		
		if(!agendamento.isNovo()){
			notasSelecionadas = cadastroAgendamentoService.buscarSaidas(agendamento);
		} 
		if(!agendamento.getItens().isEmpty()) {
			
			if(notasSelecionadasTemp!=null) {
				notasSelecionadas = verificarNotasSelecionadas(notasSelecionadasTemp);	
			}
			if(notasSelecionadas!=null) {
				agendamento.adicionarSaidas(notasSelecionadas);
			}
			atualizarStatusItemCancelado();
			agendamento = cadastroAgendamentoService.salvar(this.agendamento);
			FacesUtil.addInfoMessage("Agendamento salvo com sucesso!");
			limpar();
		}else {
			if (agendamento.getItens().isEmpty()) 
				FacesUtil.addWarningMessage("Informe pelo menos um item no agendamento!");
		}
	}

	private List<NotaFiscal> verificarNotasSelecionadas(List<NotaFiscal> notasSelecionadasTemp) {
		
		List<NotaFiscal>novaListadeNotas = new ArrayList<NotaFiscal>();
		for (NotaFiscal nf : notasSelecionadasTemp) {
			
			Optional<ItemMontagem>itemMontagemOptional =  agendamento.getItens().stream()
					.filter(i -> i.getNotaFiscal().equals(nf.getNota().toString()))
					.findAny();
			if(itemMontagemOptional.isPresent()) {
				novaListadeNotas.add(nf);
			}
		}
		return novaListadeNotas;
	}

	private void atualizarStatusItemCancelado() {
		if(!itensCancelados.isEmpty()) {
			for (ItemMontagem itemMontagem : itensCancelados) {
				cadastroAgendamentoService.atualizarStatusItem(itemMontagem);
			}
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
