package com.bokine.agendamento.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.bokine.agendamento.model.Cliente;
import com.bokine.agendamento.model.TipoPessoa;
import com.bokine.agendamento.repository.Clientes;
import com.bokine.agendamento.util.jpa.Transactional;

public class CadastroClienteService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;	
	
	public Cliente buscaCliente(Cliente clienteFilter) {
		Cliente cliente = new Cliente();
		if(!clienteFilter.getDocumentoReceitaFederal().isEmpty()){
			try{
				cliente = clientes.porCpf(clienteFilter.getDocumentoReceitaFederal());
			}catch (NonUniqueResultException e) {
			}
			catch (NoResultException n) {
			}
			if(!cliente.isNovo()){
				return cliente;
			}else{
				cliente = buscaClienteESalvaFirebird(clienteFilter);
			}        
		}else{
			if(!clienteFilter.getNome().isEmpty()){
				cliente = buscaClienteESalvaFirebird(clienteFilter);
			} 
		}
		return cliente;
	}

	private Cliente buscaClienteESalvaFirebird(Cliente cliente){
		Cliente clienteAux =  new Cliente();
		clienteAux = clientes.buscaClienteFirebird(cliente);
		cliente = salvar(clienteAux);
		return cliente;
	}

	@Transactional
	public Cliente salvar(Cliente cliente){
		cliente.setTipo(TipoPessoa.FISICA);
		cliente =  clientes.guardar(cliente);
		return cliente;
	}
	
	
}
