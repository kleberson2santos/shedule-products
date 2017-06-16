package com.algaworks.pedidovenda.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.algaworks.pedidovenda.model.Cliente;
import com.algaworks.pedidovenda.model.TipoPessoa;
import com.algaworks.pedidovenda.repository.Clientes;
import com.algaworks.pedidovenda.util.jpa.Transactional;

public class CadastroClienteService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;
	
	
	public Cliente buscaCliente(Cliente clienteFilter) {
		Cliente cliente = new Cliente();
		
			//SE TIVER CPF PROCURA NO MYSQL
			if(clienteFilter.getDocumentoReceitaFederal() != null ){
		
				cliente = clientes.porCpf(clienteFilter.getDocumentoReceitaFederal());
				if(cliente!=null){
					return cliente;
				}else{
					//SE NAO ACHAR PROCURA NO FIREBIRD
					cliente = buscaClienteESalvaFirebird(clienteFilter);
					
				}        
			}else{
				if(clienteFilter.getNome()!=null){
					cliente = buscaClienteESalvaFirebird(clienteFilter);
				} 
			}
			
			return cliente;
	}
	

	private Cliente buscaClienteESalvaFirebird(Cliente cliente){
		Cliente clienteAux =  new Cliente();
		clienteAux = clientes.buscaClienteFirebird(cliente);

		if(clienteAux!=null){
			Cliente temp = new Cliente();
			temp = cpfExistente(clienteAux.getDocumentoReceitaFederal());
			if(temp==null){
				clienteAux = salvar(clienteAux);

			}else{
				clienteAux = temp;
			}
		}
		return clienteAux;
		
	}


	private Cliente cpfExistente(String cpf) {	
		return clientes.porCpf(cpf);
	}


	@Transactional
	public Cliente salvar(Cliente cliente){
		cliente.setTipo(TipoPessoa.FISICA);
		cliente =  clientes.guardar(cliente);
		return cliente;
	}
	
	
}
