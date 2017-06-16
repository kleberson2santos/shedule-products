package com.algaworks.pedidovenda.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.algaworks.pedidovenda.model.Cliente;
import com.algaworks.pedidovenda.repository.Clientes;
import com.algaworks.pedidovenda.service.CadastroClienteService;
import com.algaworks.pedidovenda.service.NegocioException;

@Named
@ViewScoped
public class SelecaoClienteFirebirdBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;
	
	@Inject
	private CadastroClienteService cadastroClienteService;
	
	private Cliente cliente;
	
	private String nome;
	private String cpf;


	public void selecionar() {
		if(cliente!=null){
			RequestContext.getCurrentInstance().closeDialog(cliente);
		}
	}
	
	public void abrirDialogo() {
		Map<String, Object> opcoes = new HashMap<>();
		opcoes.put("modal", true);
		opcoes.put("height", 470);
		
		opcoes.put("resizable", false);		
		opcoes.put("contentWidth", 600);
		
		RequestContext.getCurrentInstance().openDialog("/dialogos/SelecaoClienteFirebird", opcoes, null);
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public List<String> completarNome(String nome) {		
		return clientes.porNomeFirebird(nome);
	}

	public List<String> completarCpf(String cpf) {
		return clientes.porCpfFirebird(cpf);
	}
	
	public void atualizarCliente() throws NegocioException {
		cliente = new Cliente();
		this.cliente.setNome(this.nome);
		this.cliente.setDocumentoReceitaFederal(this.cpf);
		
		
		System.out.println("BUSCAR CLIENTE ...");
		cliente = cadastroClienteService.buscaCliente(cliente);
		System.out.println("RETORNOU DOS BANCOS: "+cliente);
		nome = cliente.getNome();
		cpf = cliente.getDocumentoReceitaFederal();
		
		if(cliente.getId()==null){
			Cliente clienteAux = new Cliente();
				clienteAux = clientes.porCpf(cpf);
				if(clienteAux!=null){
					cliente = clienteAux;
				}
			
		}

	}	

}